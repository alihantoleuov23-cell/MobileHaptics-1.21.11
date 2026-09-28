cmake_minimum_required(VERSION 3.22.1)
project(mobilehaptics_bridge)

add_library(mobilehaptics_bridge SHARED mobilehaptics.cpp)

find_library(log-lib log)
target_link_libraries(mobilehaptics_bridge ${log-lib})

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
