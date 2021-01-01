package com.example.opengltest;
import java.nio.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class OpenGLESRenderer implements GLSurfaceView.Renderer {

    private FloatBuffer triangleVB;
    private FloatBuffer triangleCB;
    private int mProgram;
    private int maPositionHandle;
    private int maColorHandle;
    private PointCloudReader pointCloud;

    private int muMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mProjMatrix = new float[16];

    public float mAnglePitch;
    public float mAngleYaw;
    public float mAngleRoll;

    public OpenGLESRenderer(PointCloudReader pointCloudReader) {
        pointCloud = pointCloudReader;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);

        // initialize the triangle vertex array
        initShapes();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL program executables

        // get handle to the vertex shader's vPosition member
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
    }

    public void onDrawFrame(GL10 unused) {

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // Prepare the triangle data
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                (3 * 4), triangleVB);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
                (4 * 4), triangleCB);
        GLES20.glEnableVertexAttribArray(maColorHandle);


        // Create a rotation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);
        //Matrix.setRotateM(mMMatrix, 0, angle, 0, 0, 1.0f);

        // Use the mAngle member as the rotation value
        Matrix.setRotateM(mMMatrix, 0, mAngleRoll, 0, 0, 1.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 4);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coodinates
        // in the onDrawFrame() method
        //Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.perspectiveM(mProjMatrix, 0, 60f, ratio, 0, 2);
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -1,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    private void initShapes(){

        /*
        float triangleCoords[] = {
                // X, Y, Z
                -0.25f, -1f, 0, // Right bottom
                0.25f, -0.25f, 0, // Left bottom
                -0.25f,  0.25f, 0, // Right top
                0.25f, 0.25f, 0 // Left top
        };

        float triangleColors[] = {
                // R, G, B
                1.0f, 0, 0, 0, // Red
                0, 1.0f, 0, 0,// Green
                0, 0, 1.0f, 0,// Blue
                1.0f, 1.0f, 0, 0 // Yellow
        };
        */

        // initialize vertex Buffer for triangle
        ByteBuffer vbb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                pointCloud.pointCloudCoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        triangleVB = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        triangleVB.put(pointCloud.pointCloudCoords);    // add the coordinates to the FloatBuffer
        triangleVB.position(0);            // set the buffer to read the first coordinate

        ByteBuffer cbb = ByteBuffer.allocateDirect(pointCloud.pointCloudColors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        triangleCB = cbb.asFloatBuffer();
        triangleCB.put(pointCloud.pointCloudColors);
        triangleCB.position(0);

    }

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;   \n" +
            "attribute vec4 vPosition;  \n" +
            "attribute vec4 aColor;     \n" +
            "varying vec4 vColor;       \n" +
            "void main(){               \n" +
            // the matrix must be included as a modifier of gl_Position
            " gl_Position = uMVPMatrix * vPosition; \n" +
            " gl_PointSize = 15.0;      \n" +
            " vColor = aColor;          \n" +
            "}                          \n";

    private final String fragmentShaderCode =
            "precision mediump float;  \n" +
            "varying vec4 vColor;      \n" +
            "void main(){              \n" +
            " gl_FragColor = vColor;   \n" +
            "}                         \n";

    private int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}