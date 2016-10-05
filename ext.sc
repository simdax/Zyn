// so useless

+ MIDIOut{
	*findPort { arg deviceName,portName;
		^MIDIClient.destinations.detectIndex({
			|endPoint|
			endPoint.device == deviceName
			or: {endPoint.name == portName}});
	}
}
