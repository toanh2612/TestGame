package com.mygdx.spacejourney;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		commit
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new SpaceJourney(), config);
	}
}
