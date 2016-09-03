Zyn{

	//TODO replace with aconnect
	// TODO check for getting osc Port from already opened
	// TODO check for windows
	
	classvar cmdline, <port;
	classvar <oscPort;
	classvar <pid;
	
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

	// TODO manage multi instances
	
	*isConnected{
		// TODO hacking with jack_lsp
	}
	*new{ arg port;
		if (this.isOpen.not){
			r{
				this.open;
				1.wait;
				this.port_(0);
			}.play
		};
	}

	
	// TODO manage multi instance
	*port_{ arg p;
		
		if(MIDIClient.initialized.not){MIDIClient.init};

		try{MIDIOut.connect(p, MIDIClient.destinations
			.detect{|x| x.name=="ZynAddSubFX"}
		)}{"pas réussi à connecter !".warn};
		p=port;
	}
	
}