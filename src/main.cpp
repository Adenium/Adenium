#include <iostream>

int main(int argc, char* argv[])
{
    if (argc < 1) {
        std::cerr << "Usage: " << argv[0] << " <input_file>" << std::endl;
        return 1;
    }
    const char* input_file = argv[0];
    std::cout << "Input file: " << input_file << std::endl;


    return 0;
}