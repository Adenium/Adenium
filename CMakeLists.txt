cmake_minimum_required(VERSION 3.5)
project(MyProject VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED YES)
find_package(LevelDB REQUIRED)
include_directories(
    ${LevelDB_INCLUDE_DIRS}
)
link_directories(
    ${LevelDB_LIBRARY_DIRS}
)
file(GLOB_RECURSE SOURCE_FILES
    src/*.cpp
)
file(GLOB_RECURSE HEADER_FILES
    src/*.h
)
add_executable(Adenium ${SOURCE_FILES})
target_include_directories(Adenium PRIVATE HEADER_FILES)
target_link_libraries(Adenium PRIVATE
    ${LevelDB_LIBRARIES}
)
target_compile_options(Adenium PRIVATE
    -Wall
    -Wextra
    -Werror
)
install(TARGETS Adenium DESTINATION bin)
