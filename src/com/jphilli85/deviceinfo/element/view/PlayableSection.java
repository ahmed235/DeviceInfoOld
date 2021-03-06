package com.jphilli85.deviceinfo.element.view;

import android.content.Context;
import android.view.View;

import com.jphilli85.deviceinfo.R;

public abstract class PlayableSection extends CollapsibleSection {	
	public interface Callback {
		void onPlay(PlayableSection section);
		void onPause(PlayableSection section);		
	}
	
	private Callback mCallback;
	
	private boolean mIsPlaying;
	private int mPlayPauseIndex;
	
	PlayableSection(Context context, int layoutRes, int labelIndex, String label, int playPauseIndex) {
		super(context, layoutRes, labelIndex, label);		
		mPlayPauseIndex = playPauseIndex;
	}
	
	void setCallback(Callback callback) {
		mCallback = callback;
		if (callback != null) {
			showPlayPause();
			setIcon(R.drawable.holo_dark_play, mPlayPauseIndex);
			setListener(mPlayPauseIndex, new PlayPauseListener());
		}
	}
	
	public void showPlayPause() {
		getHeader().getChildAt(mPlayPauseIndex).setVisibility(View.VISIBLE);
	}
	
	public void hidePlayPause() {
		getHeader().getChildAt(mPlayPauseIndex).setVisibility(View.GONE);
	}
	
	Callback getCallback() {
		return mCallback;
	}

	protected void pause() {
		if (!mIsPlaying) return;
		if (mCallback != null) mCallback.onPause(this);
		setPlayIcon();
		mIsPlaying = false;	
	}
	
	protected void play() {
		if (mIsPlaying) return;
		if (mCallback != null) mCallback.onPlay(this);
		setPauseIcon();
		mIsPlaying = true;		
	}
	
	protected void setPauseIcon() {
		setIcon(R.drawable.holo_dark_pause, mPlayPauseIndex);
	}
	
	protected void setPlayIcon() {
		setIcon(R.drawable.holo_dark_play, mPlayPauseIndex);
	}
	
	protected boolean isPlaying() {
		return mIsPlaying;
	}

	private class PlayPauseListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (mIsPlaying) pause();
			else play();
		}
	}
}
