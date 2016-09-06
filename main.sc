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
		oscPort=9000
	}
	*isOpen{
		^"ps -e | grep zynaddsubfx".unixCmdGetStdOut.notEmpty;
	}
	*open{ arg oscPort=9000;
		if(this.isOpen){"already opened".warn}
		//else
		{
			pid=("zynaddsubfx -P" ++ oscPort ++" -I alsa --auto-connect").unixCmd
			// TODO what's the heck with pid ?
			+ 2;
		}
	}
	*kill{
		pid !? {("kill "++pid).unixCmd}
	}
	// TODO manage multi instances
	
	*isConnected{
		// TODO hacking with jack_lsp
	}
	*new{ arg port=0;
		// TODO: bug etrange avec midiclient.init
		// DO NOT USE
		if (this.isOpen.not){
			this.open;
		};
		this.port_(port);
		this.test;
	}

	
	// TODO manage multi instance
	*port_{ arg p;
		if(
			try{MIDIClient.destinations
				.select({|x| x.name==this.nam}).isEmpty.postln}
			{true}, {
			 MIDIClient.init; 
		});
		try{MIDIOut.connect(p, MIDIClient.destinations
			.detect{|x| x.name==Zyn.nam}
		);
			port=p; }
		{"pas réussi à connecter !".warn};
		
	}
	// OSC interface
	*panic{
		if(this.isOpen.not){^nil};
		NetAddr("localhost", oscPort).sendMsg("/Panic")
	}
	*test{ arg port=0;
		var t={MIDIOut(port).noteOn(0, 60, 60)};
		r{t.value; 1.wait; Zyn.panic}.play

	}
//TODO
	// *loadXMZ{
	// 	NetAddr("localhost", oscPort).sendMsg("/load_xmz", )
	// }

	// GUI
	*gui{
		Button().front.action_{
			Zyn.test(port)
		} 
	}

	
}