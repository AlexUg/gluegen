
D_ARGS="-Djogamp.debug.JNILibLoader -Djogamp.debug.TempFileCache -Djogamp.debug.JarUtil -Djogamp.debug.TempJarCache -Djogamp.debug.IOUtil"
#D_ARGS="-Djogamp.debug.ProcAddressHelper -Djogamp.debug.NativeLibrary -Djogamp.debug.NativeLibrary.Lookup"
#D_ARGS="-Djogamp.debug.JNILibLoader -Djogamp.debug.TempFileCache -Djogamp.debug.JarUtil -Djogamp.debug.TempJarCache -Djogamp.debug.NativeLibrary -Djogamp.debug.IOUtil"

#T_CLASS="com.jogamp.opengl.JoglVersion"
T_CLASS="com.jogamp.newt.opengl.GLWindow"

java -cp jogl-multi.jar $D_ARGS $T_CLASS 2>&1 | tee run-multi.log
