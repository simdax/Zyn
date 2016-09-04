Zyn{

	//TODO replace with aconnect
	// TODO check for getting osc Port from already opened
	// TODO check for windows
	
	classvar cmdline, <port;
	classvar <oscPort;
	classvar <pid;
	classvar <nam;

	*initClass{
		nam="ZynAddSubFX";
	}
	*isOpen{
		^"ps -e | grep zynaddsubfx".unixCmdGetStdOut.notEmpty;
	}
	*open{ arg oscPort=9000;
		if(this.isOpen){"already opened".warn}
		//else
		{
			pid=("zynaddsubfx -P" ++ oscPort ++" -I alsa --auto-connect -l /home/simdax/Musique/zyn.xmz").unixCmd
			// TODO what's the heck with pid ?
			+ 2;
		}
	}
	*kill{
		pid !? {("kill "++pid).unixCmd}
	}
	*panic{
		if(this.isOpen.not){^nil};
		NetAddr("localhost", NetAddr.langPort).sendMsg("/Panic")
	}
	// TODO manage multi instances
	
	*isConnected{
		// TODO hacking with jack_lsp
	}
	*new{ arg port;
		// TODO: bug etrange avec midiclient.init
		// DO NOT USE
		if (this.isOpen.not){
			this.open;
		};
		this.port_(port);
	}

	
	// TODO manage multi instance
	*port_{ arg p;
		if(MIDIClient.destinations.select({|x| x.name==this.nam}).isEmpty.postln, {
			 MIDIClient.init; 
		});
		try{MIDIOut.connect(p, MIDIClient.destinations
			.detect{|x| x.name==Zyn.nam}
		);
			p=port; }
		{"pas réussi à connecter !".warn};
		
	}
	
}