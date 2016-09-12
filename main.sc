Zyn{

	//TODO replace with aconnect
	// TODO check for getting osc Port from already opened
	// TODO check for windows

	classvar cmdline, <port;
	classvar <>oscPort;
	classvar <pid;
	classvar <nam;

	*initClass{
		Class.initClassTree(OSCdef);
		nam="ZynAddSubFX";
		cmdline=Platform.case(
			\linux, {"zynaddsubfx -P" ++ oscPort ++" -I alsa --auto-connect"},
			\windows, {"start zynaddsubfx"}
		);
		oscPort=9000;

		(..16).do{ arg i;
		OSCdef.newMatching(\zyn++i, { arg reponse;
		"part "++i++" allumée ? => ".post; reponse[1].postln;
		}, ("/part"++i++"/Penabled"))
		}

	}
	*isOpen{
		^"ps -e | grep zynaddsubfx".unixCmdGetStdOut.notEmpty;
	}
	*open{ arg oscP=9000;
		if(this.isOpen){("already opened with port "++oscPort).warn}
		//else
		{
			oscPort=oscP;
			pid=cmdline.unixCmd
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
		r{
			if (this.isOpen.not){
				this.open;
				2.wait;
			};
			this.port_(port);
			this.test;
			2.wait;
			//wait for test to finish
		}.play
	}


	// TODO manage multi instance
	*port_{ arg p;
		if(
			try{MIDIClient.destinations
				.select({|x| x.name==this.nam}).isEmpty.postln}
			{true}, {
			 MIDIClient.init;
		});
		try{
			MIDIOut.connect(p,
				MIDIClient.destinations.detect{|x| x.name==Zyn.nam}
			);
			port=p; }
		{"pas réussi à connecter !".warn};

	}
	// OSC interface
	*panic{
		if(this.isOpen.not){^nil};
		this.send("/Panic")
	}
	*test{
		var t={MIDIOut(port).noteOn(0, 60, 60)};
		r{t.value; 1.wait; Zyn.panic}.play

	}
	*send{ arg ... msg;
		NetAddr("localhost", oscPort).sendMsg(*msg)
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