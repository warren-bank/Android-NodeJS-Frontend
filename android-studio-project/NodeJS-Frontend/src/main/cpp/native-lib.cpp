#include <jni.h>
#include <string>
#include <cstdlib>
#include "node.h"

// =====
// docs:
// =====
// * https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/design.html#resolving_native_method_names
//     [Java 8 JNI spec] Chapter 2: Design Overview - Resolving Native Method Names
// =====

// setenv polyfill for API < 21
extern "C" jint JNICALL
Java_com_github_warren_1bank_nodejs_1frontend_helpers_NodeJsAppRunner_setenv(
        JNIEnv *env,
        jobject /* this */,
        jstring name,
        jstring value,
        jint overwrite) {
    const char* name_ = env->GetStringUTFChars(name, 0);
    const char* value_ = env->GetStringUTFChars(value, 0);

    return jint(setenv(name_, value_, overwrite));
}

// node.js libUV requires all arguments to be on contiguous memory
extern "C" jint JNICALL
Java_com_github_warren_1bank_nodejs_1frontend_helpers_NodeJsAppRunner_startNodeWithArguments(
        JNIEnv *env,
        jobject /* this */,
        jobjectArray arguments) {

    //argc
    jsize argument_count = env->GetArrayLength(arguments);

    //Compute byte size need for all arguments in contiguous memory.
    int c_arguments_size = 0;
    for (int i = 0; i < argument_count ; i++) {
        c_arguments_size += strlen(env->GetStringUTFChars((jstring)env->GetObjectArrayElement(arguments, i), 0));
        c_arguments_size++; // for '\0'
    }

    //Stores arguments in contiguous memory.
    char* args_buffer=(char*)calloc(c_arguments_size, sizeof(char));

    //argv to pass into node.
    char* argv[argument_count];

    //To iterate through the expected start position of each argument in args_buffer.
    char* current_args_position=args_buffer;

    //Populate the args_buffer and argv.
    for (int i = 0; i < argument_count ; i++)
    {
        const char* current_argument = env->GetStringUTFChars((jstring)env->GetObjectArrayElement(arguments, i), 0);

        //Copy current argument to its expected position in args_buffer
        strncpy(current_args_position, current_argument, strlen(current_argument));

        //Save current argument start position in argv
        argv[i] = current_args_position;

        //Increment to the next argument's expected position.
        current_args_position += strlen(current_args_position)+1;
    }

    //Start node, with argc and argv.
    return jint(node::Start(argument_count,argv));
}
