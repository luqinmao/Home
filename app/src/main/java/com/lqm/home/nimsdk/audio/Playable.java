package com.lqm.home.nimsdk.audio;

public interface Playable {
	long getDuration();
	String getPath();
	boolean isAudioEqual(Playable audio);
}