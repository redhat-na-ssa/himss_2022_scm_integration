#include "am3x.h"
#include "util.h"
#include <iostream>
#include <string>

std::string generate_am3x(int number_of_rows) {
    std::string location[3] = { "VA HOSPITAL", "WHSE 81-H", "NONE" };
    std::string incomplete_flag_indicator[2] = { "Y", "N" };
    std::string manufacturer_common_name[39] = { "TEVA PHARMACEUTICAL INDUSTRIES LTD", "HIKMA PHARMACEUTICALS PLC", "MERCK", "DURA PHARMACEUTICALS", "JANSSEN PHARMACEUTICALS", "FRESENIUS KABI", "ROCHE DIAGNOSTICS", "HOSPIRA", "MEDLINE INDUSTRIES", "AMERICAN NATIONAL STANDARDS INSTITUTE", "ICP MEDICAL", "GLAXOSMITHKLINE", "MOLDEX METRIC", "AKORN", "TVI", "SMS", "NORTH SAFETY PRODUCTS", "MERIDIAN MEDICAL TECHNOLOGIES", "3M", "EMERGENT PROTECTIVE PRODUCTS USA", "ONGUARD", "PEKE LOGISTICS LLC", "AMERICAN HEALTH PACKAGING", "ANBEX", "O & M HALYARD", "MC JOHNSON", "SHOWA-BEST GLOVE", "TRIOMED INNOVATIONS", "THERMO FISHER SCIENTIFIC", "GALLS", "BOSMA ENTERPRISES", "ANSELL", "BEACON LIGHTHOUSE", "CARDINAL HEALTH", "UNICOR", "ADDRESSOGRAPH", "PRESTIGE AMERITECH LTD", "ALPHA PRO TECH", "W W GRAINGER" };
    std::string lot_number[9] = { "64803", "6005144", "U4091", "6578902/64409", "8M1529", "3034613", "4TF740/454729", "4TF740/4S4729", "6532014502057" };
    std::string manufacturer_name[10] = { "BOSMA", "GOJO", "ICP MEDICAL", "HALYARD", "MOLDEX", "3M", "CARDINAL HEALTH", "UNICOR", "TECH STYLE", "ALPHA PROTECH" };

    std::string data = "";
    for (int i = 0; i < number_of_rows; i++) {
        data += random_number_string(13) + DELIMITER; // item_id
        data += random_alphanumeric_string(6) + DELIMITER; // sub_organization_identifier_sub_org_id
        data += random_alphanumeric_string(4) + DELIMITER; // assemblage_identifier
        data += random_alphanumeric_string(2) + DELIMITER; // assemblage_increment
        data += random_number_string(2) + DELIMITER; // assemblage_sub_assemblage
        data += "1" + DELIMITER; // assemblage_instance_num
        data += random_alpha_string(3) + DELIMITER; // stratification_state
        data += std::to_string(rand()) + DELIMITER; // on_hand_quantity
        data += "0" + DELIMITER; // equipment_control_number
        data += location[random_index(sizeof(location), sizeof(location[0]))] + DELIMITER;
        data += incomplete_flag_indicator[random_index(sizeof(incomplete_flag_indicator), sizeof(incomplete_flag_indicator[0]))] + DELIMITER;
        data += manufacturer_common_name[random_index(sizeof(manufacturer_common_name), sizeof(manufacturer_common_name[0]))] + DELIMITER;
        data += std::to_string(rand()%999) + DELIMITER; // excess_quantity_remaining
        data += random_date_string() + DELIMITER; // expiration_date
        data += lot_number[random_index(sizeof(lot_number), sizeof(lot_number[0]))] + DELIMITER;
        data += "" + DELIMITER; // +expiration_extension_date, not generating bc of the business logic of needing expiration_extension_date after expiration_date
        data += manufacturer_name[random_index(sizeof(manufacturer_name), sizeof(manufacturer_name[0]))] + DELIMITER;
        data += random_number_string(5); // product_number;
        data += '\n';
    }
    return data;
}

std::string generate_am3x_filename(void) {
    // example: AM3X-365115-1002-2-2021285.txt
    const std::string separator = "-";
    return "AM3X" + separator + random_number_string(6) + separator + random_number_string(4) + "-2-" + random_date_string() + ".txt";
}
