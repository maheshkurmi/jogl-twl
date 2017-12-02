package de.matthiasmann.twl.renderer.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;

import de.matthiasmann.twl.GUI;

public final class Util {
	/** No c'tor */
	private Util() {
	}

	/**
	 * Throws OpenGLException if glGetError() returns anything else than GL_NO_ERROR
	 *
	 */
	public static void checkGLError() throws OpenGLException {
		GL2 gl=GUI.gl;
		/*GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		
		if ( caps.DEBUG && GLContext.getCapabilities().tracker.isBeginEnd() ) // Do not call GetError inside a Begin/End pair.
			return;
			*/
		int err =gl.glGetError();
		if ( err != GL2.GL_NO_ERROR ) {
			throw new OpenGLException(err);
		}
	}

	/**
	 * Translate a GL error code to a String describing the error
	 */
	public static String translateGLErrorString(int error_code) {
		switch (error_code) {
			case GL2.GL_NO_ERROR:
				return "No error";
			case GL2.GL_INVALID_ENUM:
				return "Invalid enum";
			case GL2.GL_INVALID_VALUE:
				return "Invalid value";
			case GL2.GL_INVALID_OPERATION:
				return "Invalid operation";
			case GL2.GL_OUT_OF_MEMORY:
				return "Out of memory";
			case GL2.GL_TABLE_TOO_LARGE:
				return "Table too large";
			case GL2.GL_INVALID_FRAMEBUFFER_OPERATION:
				return "Invalid framebuffer operation";
			default:
				return null;
		}
	}
}