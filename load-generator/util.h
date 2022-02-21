#ifndef UTIL_H
#define UTIL_H

#include <string>
int random_index(unsigned long full_size, unsigned long single_size);

std::string random_number_string(int length);
std::string random_alphanumeric_string(int length);
std::string random_alpha_string(int length);
std::string random_date_string(void);
int tar(std::string filename, std::string glob);
int parse_int(char *opt);
void write_to_file(std::string filename, std::string data);
const std::string DELIMITER="|";

#endif
