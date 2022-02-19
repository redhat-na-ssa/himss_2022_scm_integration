#include <iostream>
#include <string>
#include <chrono>
#include <cstdio>
#include <time.h>
#include <unistd.h>
#include <filesystem>
#include "am3x.h"
#include "detm.h"
#include "util.h"

int main(int argc, char **argv)
{
    std::chrono::high_resolution_clock::time_point start = std::chrono::high_resolution_clock::now();
    srand(time(0));

    try
    {
        int number_of_files = 1;
        int number_of_rows = 200;
        std::string tar_filename = random_date_string() + "-" + std::to_string(rand());
        std::string output_dir = "output";
        int opt;
        bool keep_files = false;
        while ((opt = getopt(argc, argv, ":hkr:n:f:")) != -1)
        {
            switch (opt)
            {
            case 'h':
            {
                // help
                const std::string help_text =
R"(
This program has a few optional options.
  -h: This help screen
  -k: Keep txt files that are generated, by default they are deleted after tar is created
  -r <number_of_rows>: Number of rows per file, default is 200
  -n <number_of_files>: Number of files per tar. The real data export has 1 file per tar, this is the default
  -f <name>: Base name of tar, will have prefix of tablename (eg. AM3X or DETM), default is random string
  -o <dir_name>: Output directory where tar files will go, default is 'output'.  Will not create if exists
)";
                std::cout << help_text << std::endl;
                return 0;
            }
            case 'k':
            {
                // keep files
                keep_files = true;
                break;
            }
            case 'r':
            {
                // rows per file
                number_of_rows = parse_int(optarg);
                break;
            }
            case 'n':
            {
                // number of files in tar
                number_of_files = parse_int(optarg);
                break;
            }
            case 'f':
            {
                // filename of tgz
                std::string new_filename(optarg);
                tar_filename = new_filename;
                break;
            }
            case 'o':
            {
                // output directory
                std::string new_output_dir(optarg);
                output_dir = new_output_dir;
                break;
            }
            case ':':
            {
                std::cout << "Invalid option passed in. If passing in -r, -f, -n they require a param." << std::endl;
                return 0;
            }
            }
        }

        std::string test_am3x_filenames_string = "";
        std::string test_detm_filenames_string = "";
        std::string test_am3x_filenames[number_of_files];
        std::string test_detm_filenames[number_of_files];
        std::string test_am3x_tar_filename = "AM3X-" + tar_filename + "-scm.tgz";
        std::string test_detm_tar_filename = "DETM-" + tar_filename + "-scm.tgz";

        for (int i = 0; i < number_of_files; i++)
        {
            const std::string am3x_filename = generate_am3x_filename();
            const std::string detm_filename = generate_detm_filename();
            write_to_file(am3x_filename, generate_am3x(number_of_rows));
            write_to_file(detm_filename, generate_detm(number_of_rows));
            test_am3x_filenames_string += am3x_filename + " ";
            test_detm_filenames_string += detm_filename + " ";
            test_am3x_filenames[i] = am3x_filename;
            test_detm_filenames[i] = detm_filename;
        }

        // setup output dir
        if (!std::filesystem::exists(output_dir)) {
            std::filesystem::create_directory(output_dir);
        }

        // create tar
        tar(test_am3x_tar_filename, test_am3x_filenames_string);
        tar(test_detm_tar_filename, test_detm_filenames_string);

        // clean up
        if (!keep_files)
        {
            for (int i = 0; i < number_of_files; i++)
            {
                std::remove(test_am3x_filenames[i].c_str());
                std::remove(test_detm_filenames[i].c_str());
            }
        }

        // move tars
        std::filesystem::copy(test_am3x_tar_filename, output_dir + "/" + test_am3x_tar_filename);
        std::filesystem::copy(test_detm_tar_filename, output_dir + "/" + test_detm_tar_filename);
        std::remove(test_am3x_tar_filename.c_str());
        std::remove(test_detm_tar_filename.c_str());

        // how long things took
        std::chrono::high_resolution_clock::time_point stop = std::chrono::high_resolution_clock::now();
        std::chrono::milliseconds duration = std::chrono::duration_cast<std::chrono::milliseconds>(stop - start);
        std::cout << "Completed in " << duration.count() << "ms" << std::endl;
        std::cout << "Generated: " << std::endl;
        std::cout << "\t" << test_am3x_tar_filename << std::endl;
        std::cout << "\t" << test_detm_tar_filename << std::endl;
    }
    catch (int e)
    {
        std::cout << "An exception occurred. Exception Number: " << e << std::endl;
        return 1;
    }

    return 0;
}
