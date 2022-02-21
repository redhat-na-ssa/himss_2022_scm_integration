#include "util.h"
#include <cmath>
#include <string>
#include <iostream>
#include <sstream>
#include <fstream>

int random_index(unsigned long full_size, unsigned long single_size) {
	if (full_size == 0) {
		return 0;
	}
	return (rand()%full_size/single_size);
}

std::string random_number_string(int length) {
	std::string str = "";
	for (int i = 0; i < length; i++) {
		str += std::to_string(rand()%10);
	}
	return str;
}

std::string random_alphanumeric_string(int length) {
	std::string str = "";
	for (int i = 0; i < length; i++) {
		if (rand()%2 == 0) {
			str += 'A' + rand()%26;
		} else {
			str += std::to_string(rand()%10);
		}
	}
	return str;
}

std::string random_alpha_string(int length) {
	std::string str = "";
	for (int i = 0; i < length; i++) {
		str += 'A' + rand()%26;
	}
	return str;
}

std::string random_date_string(void) {
	int years[72] = { };
	for (int i = 1950; i < 2022; i++) {
		years[i - 1950] = i;
	}

	int random_year = years[rand()%72];
	int random_month = (rand()%12)+1;
	int random_days = (rand()%30)+1;

	std::string month_string = std::to_string(random_month);
	if (random_month < 10) {
		month_string = '0' + month_string;
	}

	std::string day_string = std::to_string(random_days);
	if (random_days < 10) {
		day_string = '0' + day_string;
	}
	return std::to_string(random_year) + month_string + day_string;
}

int tar(std::string filename, std::string glob) {
	const std::string command = "tar -czf " + filename + " " + glob;
	return std::system(command.c_str());
}

int parse_int(char *opt)
{
    std::stringstream ss(opt);
    int i = 0;
    if (ss >> i)
        return i;
    else
        return 0;
}

void write_to_file(std::string filename, std::string data)
{
    std::ofstream file;
    file.open(filename, std::ios::out | std::ios::trunc);
    file << data;
    file.close();
}
