# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(TARGET icc)
set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR})

include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}/
        #        opencv/include
)

#add_library(libopencv_java4 SHARED IMPORTED)
#set_target_properties(libopencv_java4 PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_CURRENT_SOURCE_DIR}/opencv/${ANDROID_ABI}/libopencv_java4.so)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

aux_source_directory(${SRC_DIR} DIR_LIB_SOURCE)

add_library(${TARGET} SHARED ${DIR_LIB_SOURCE})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

target_link_libraries(${TARGET} log android)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#if(${ANDROID_ABI} STREQUAL x86 OR ${ANDROID_ABI} STREQUAL x86_64)
#target_link_libraries(${TARGET} opencv_imgproc opencv_core ippiw ippicv ittnotify tbb cpufeatures)
#else()
#target_link_libraries(${TARGET})
#endif()

