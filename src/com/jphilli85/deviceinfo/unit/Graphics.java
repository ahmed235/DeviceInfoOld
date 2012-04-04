package com.jphilli85.deviceinfo.unit;

import java.util.LinkedHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;

// TODO use multiple egl contexts (1 gives different results than 2,
// even if still using GLES20 methods.

// TODO more values & limits
public class Graphics extends Unit implements GLSurfaceView.Renderer {
	public static final float OPENGLES_VERSION_10 = 1.0f;
	public static final float OPENGLES_VERSION_11 = 1.1f;
	public static final float OPENGLES_VERSION_20 = 2.0f;
	

	public interface OnGLSurfaceViewCreatedListener {
		void onGLSurfaceViewCreated();
	}
	
	private final float mOpenGlesVersion;

	private OnGLSurfaceViewCreatedListener mListener;
	private OpenGles mOpenGles;
	private GLSurfaceView mSurfaceView;
	
	public Graphics(GLSurfaceView surfaceView) {
		mSurfaceView = surfaceView;
		mOpenGlesVersion = findOpenGlesVersion();	
		mListener = null;

		if (Build.VERSION.SDK_INT >= 8) {
			surfaceView.setEGLContextClientVersion((int) mOpenGlesVersion);
		}

		surfaceView.setRenderer(this);
	}
	
	public void setOnGLSurfaceViewCreatedListener(OnGLSurfaceViewCreatedListener l) {
		mListener = l;
	}
	
	private float findOpenGlesVersion() {
		boolean gles10 = true;
		boolean gles11 = true;
		boolean gles20 = true;
		float ver = 0;
		
		try { Class.forName("android.opengl.GLES10"); }
		catch (ClassNotFoundException ignored) {
			gles10 = false;
		}
		
		try { Class.forName("android.opengl.GLES11"); }
		catch (ClassNotFoundException ignored) {
			gles11 = false;
		}
		
		try { Class.forName("android.opengl.GLES20"); }
		catch (ClassNotFoundException ignored) {
			gles20 = false;
		}
		
		if (gles10) ver = OPENGLES_VERSION_10;
		if (gles11) ver = OPENGLES_VERSION_11;
		if (gles20) ver = OPENGLES_VERSION_20;
		
		return ver;
	}
	
	public OpenGles getOpenGles() {
		return mOpenGles;
	}
	
	/** Get the highest supported OpenGL ES version.
	 * This will be available immediately after instantiation
	 * whereas getOpenGles() will only be ready after the GLSurfaceView
	 * has been created.	 
	 */
	public float getOpenGlesVersion() {
		return mOpenGlesVersion;
	}
	
	
	private abstract class OpenGles {
		protected final float mOpenGlesVersion;
		
		protected String mRenderer;
		protected String mVersion;
		protected String mVendor;
		protected int mMaxTextureSize;
				
		protected String[] mExtensions;
		
		public OpenGles() {
			mOpenGlesVersion = Graphics.this.mOpenGlesVersion;
		}
		
		public float getOpenGlesVersion() {
			return mOpenGlesVersion;
		}
		
		public String getRenderer() {
			return mRenderer;
		}
		
		public String getVersion() {
			return mVersion;
		}
		
		public String getVendor() {
			return mVendor;
		}
		
		public int getMaxTextureSize() {
			return mMaxTextureSize;
		}	

		public String[] getExtensions() {
			return mExtensions;
		}
		
		protected int getInt(int glConst) {
			int[] placeholder = {0};		
			if (mOpenGlesVersion == OPENGLES_VERSION_10) 
				GLES10.glGetIntegerv(glConst, placeholder, 0);
			else if (mOpenGlesVersion == OPENGLES_VERSION_11) 
				GLES11.glGetIntegerv(glConst, placeholder, 0);
			else if (mOpenGlesVersion == OPENGLES_VERSION_20) 
				GLES20.glGetIntegerv(glConst, placeholder, 0);
			return placeholder[0];
		}
		
		public LinkedHashMap<String, String> getContents() {
			LinkedHashMap<String, String> contents = new LinkedHashMap<String, String>();
			
			contents.put("OpenGL ES Version", String.valueOf(getOpenGlesVersion()));
			contents.put("OpenGL ES Renderer", getRenderer());
			contents.put("OpenGL ES Version", getVersion());
			contents.put("OpenGL ES Vendor", getVendor());
			contents.put("OpenGL ES MaxTextureSize", String.valueOf(getMaxTextureSize()));			
			if (mExtensions != null) {
				for (int i = 0; i < mExtensions.length; ++i) {
					contents.put("OpenGL ES Extension " + i, mExtensions[i]);
				}
			}
			return contents;
		}
	}
	
	private class OpenGles10 extends OpenGles {
		protected int mMaxTextureUnits;
		protected int mMaxTextureStackDepth;
		
		public OpenGles10() {
			super();
			mRenderer = GLES10.glGetString(GLES10.GL_RENDERER);
			mVersion = GLES10.glGetString(GLES10.GL_VERSION);
			mVendor = GLES10.glGetString(GLES10.GL_VENDOR);
			mMaxTextureSize = getInt(GLES10.GL_MAX_TEXTURE_SIZE);				
			mExtensions = GLES10.glGetString(GLES10.GL_EXTENSIONS).split(" ");
				
			mMaxTextureUnits = getInt(GLES10.GL_MAX_TEXTURE_UNITS);		
			mMaxTextureStackDepth = getInt(GLES10.GL_MAX_TEXTURE_STACK_DEPTH);
		}
		
		public int getMaxTextureUnits() {
			return mMaxTextureUnits;
		}
		
		public int getMaxTextureStackDepth() {
			return mMaxTextureStackDepth;
		}

		@Override
		public LinkedHashMap<String, String> getContents() {
			LinkedHashMap<String, String> contents = super.getContents();
			contents.put("OpenGL ES MaxTextureUnits", String.valueOf(getMaxTextureUnits()));
			contents.put("OpenGL ES MaxTextureStackDepth", String.valueOf(getMaxTextureStackDepth()));
			return contents;
		}
	}
	
	private class OpenGles11 extends OpenGles10 {
		public OpenGles11() {
			super();
		}		
	}
	
	private class OpenGles20 extends OpenGles {
		protected int mMaxTextureImageUnits;
		protected int mMaxRenderBufferSize;

		public OpenGles20() {
			super();
			mRenderer = GLES20.glGetString(GLES20.GL_RENDERER);
			mVersion = GLES20.glGetString(GLES20.GL_VERSION);
			mVendor = GLES20.glGetString(GLES20.GL_VENDOR);
			mMaxTextureSize = getInt(GLES20.GL_MAX_TEXTURE_SIZE);							
			mExtensions = GLES20.glGetString(GLES20.GL_EXTENSIONS).split(" ");
			
			mMaxTextureImageUnits = getInt(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS);
			mMaxRenderBufferSize = getInt(GLES20.GL_MAX_RENDERBUFFER_SIZE);
		}
		
		public int getMaxTextureImageUnits() {
			return mMaxTextureImageUnits;
		}
		
		public int getMaxRenderBufferSize() {
			return mMaxRenderBufferSize;
		}

		@Override
		public LinkedHashMap<String, String> getContents() {
			LinkedHashMap<String, String> contents = super.getContents();
			contents.put("OpenGL ES MaxTextureImageUnits", String.valueOf(getMaxTextureImageUnits()));
			contents.put("OpenGL ES MaxRenderBufferSize", String.valueOf(getMaxRenderBufferSize()));
			return contents;
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {		
		if (mOpenGlesVersion == OPENGLES_VERSION_10) mOpenGles = new OpenGles10();
		else if (mOpenGlesVersion == OPENGLES_VERSION_11) mOpenGles = new OpenGles11();
		else if (mOpenGlesVersion == OPENGLES_VERSION_20) mOpenGles = new OpenGles20();

		// Let the caller know the surface has been created.
		if (mListener != null) mListener.onGLSurfaceViewCreated();
		// No intention of drawing anything, just gathering info.
		//mSurfaceView.onPause();
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		
	}

	
	@Override
	public LinkedHashMap<String, String> getContents() {
		if (mOpenGles == null) {
			LinkedHashMap<String, String> contents = new LinkedHashMap<String, String>();
			contents.put("OpenGL ES Version", String.valueOf(getOpenGlesVersion()));
			return contents;
		}
		else return getOpenGles().getContents();
	}
}
