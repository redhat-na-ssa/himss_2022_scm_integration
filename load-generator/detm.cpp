#include "detm.h"
#include "util.h"
#include <iostream>
#include <string>

std::string generate_detm(int number_of_rows) {
    std::string device_code[90] = { "L0059", "L0060", "L0061", "L0062", "L0063", "L0064", "L0065", "L0066", "L0067", "L0068", "L0069", "L0070", "L0071", "L0072", "L0080", "L0081", "L0082", "L0083", "L0084", "L0085", "L0075", "L0076", "L0077", "L0078", "L0079", "L0088", "L0073", "L0086", "L0087", "L0090", "L0001", "L0002", "L0003", "L0004", "L0005", "L0006", "L0007", "L0008", "L0009", "L0010", "L0011", "L0012", "L0013", "L0014", "L0015", "L0016", "L0017", "L0018", "L0019", "L0020", "L0021", "L0022", "L0023", "L0024", "L0025", "L0026", "L0027", "L0028", "L0029", "L0030", "L0031", "L0032", "L0033", "L0034", "L0035", "L0036", "L0037", "L0038", "L0039", "L0040", "L0041", "L0042", "L0043", "L0044", "L0045", "L0046", "L0047", "L0048", "L0049", "L0050", "L0051", "L0052", "L0053", "L0054", "L0055", "L0056", "L0057", "L0058", "L0074", "L0089" };
    std::string device_description[90] = { "LOCKER", "LENS SET", "TABLE, FOLDING", "FITTING/ADAPTER, PIN-INDEXED", "TESTER, LAN", "BRIDGE, WIRELESS", "PRINTER, THERMAL", "VEHICLES, TRACTOR", "COLLECTOR, BONE DUST", "STRETCHER, MOBILE AMBULANCE", "OBTURATORS, ENDOSCOPIC", "RF THERAPY SYSTEM, TISSUE ABLATION,ENDOMETRIAL", "HANDPIECE, ENDODONTIC", "ROTARY INSTRUMENT SYSTEM", "LOUPE", "LIGHT SOURCE", "LIGHT SOURCE, DENTAL LOUPE", "IONTOPHORESIS UNIT, DRUG DELIVERY", "EXERCISER, SHOULDER", "HANDPIECE", "VAN PATIENT TRANSPORT", "SLIDE MOUNTER", "COMPUTER WORK STATION", "CLEANER, DENTAL HANDPIECE", "SMARTBOARD", "EXERCISERS, COMPUTER AIDED TRAINING", "FOOD SERVICE EQUIPMENT, FLOOR GRILL", "LENS TINTING  UNIT", "CONTROLLER, TINT GRADIENT, LENS", "DENTAL PROSTHESIS 3D PRINTER", "PRINTER", "PRINTER, DIGITAL", "TAPE BACKUP DRIVE", "PRINTER, LASER", "PRINTER, INK JET", "SCANNER, COMPUTER", "MONITORS, COMPUTER", "COMPACT DISK", "PRINTER, COLOR", "DOCKING STATION", "LAN SWITCH", "SNOW BLOWER", "MODEM", "CALCULATOR", "PROJECTORS, OVERHEAD", "BUFFERS, FLOOR", "HAND TRUCK, PALLET", "RANGE, KITCHEN", "READER, MICROCAPILLARY", "RECEIVER", "RECEIVER, VOICE", "RECORD STATION", "RECORDER/TRANSCIBER", "RECORDERS, MICROCASSETTE", "RECORDERS, OTHER", "TACHOMETER", "REFRACTOMETERS, LABORATORY, URINE", "RELIGIOUS EQUIPMENT", "MOUSE", "ARTICULATORS, DENTAL", "BEDS, SURGICAL", "CAMERAS, OTHER", "CONTROLLERS, CAMERA", "SYRINGE CALIBRATOR", "CALIBRATOR", "CABINETS", "TESTER, BRIGHTNESS ACUITY", "DEHUMIDIFIER", "DRILLS, POWER OTHER", "TINTING MACHINES, LENS", "WARMERS, FRAME", "LIGHTS, HEAD", "COMPUTERIZED ENGRAVER", "PROBE, ULTRASONIC", "TRANSDUCER, ULTRASONIC", "CHAIRS, ISOKINETIC", "HEAT LAMP", "FLOWMETERS, PEAK FLOW", "STEAM KETTLE", "CAMCORDER", "MONITORS, VIDEO", "MODULAR MASS DECON SYSTEM", "NEONATAL/ADULT NIBP/PULSE OXIMETRY MODULE", "PRESSURE MONITORS, BLOOD, GENERAL/NON-INVASIVE", "ULTRASOUND WATT METER", "SOUP/SALAD BAR", "TRAY LINE ASSEMBLY UNIT", "CONVEYOR TRAY", "CROWN MOLDING MACHINE", "SERVER, VIDEO COMMUNICATION" };
    std::string risk_level_type_code[4] = { "4", "1", "3", "2" };
    std::string device_class_code[62] = { "L0020", "L0021", "L0022", "L0023", "15605", "C5114", "L0024", "L0025", "13814", "L0026", "11490", "17953", "15652", "12340", "12185", "11623", "L0027", "C0254", "10977", "15013", "L0005", "24763", "C5037", "C0077", "17464", "21680", "L0003", "L0004", "L0001", "L0006", "L0007", "L0008", "L0009", "L0010", "L0011", "15595", "15169", "L0012", "L0013", "10342", "10549", "11009", "L0014", "15563", "10526", "L0015", "11329", "L0016", "15610", "12347", "L0017", "L0018", "10787", "11746", "L0019", "18518", "12636", "16762", "14276", "60000", "C5049", "C0279" };
    std::string accountable_equipment_code[2] = { "N", "Y" };
    std::string maintenance_required_indicator[2] = { "N", "Y" };
    std::string life_expectancy[9] = { "7", "8", "15", "10", "6", "5", "4", "12", "3" };
    std::string delete_indicator[2] = { "N", "Y" };
    std::string specialty_code[17] = { "EM", "OB", "SUR", "DEN", "PT", "PHO", "RAD", "IMO", "OPF", "ADM", "NUT", "LAB", "ENG", "PUL", "OPH", "OPT", "CAR" };
    std::string federal_supply_class_fsc[25] = { "6530", "6515", "6520", "6740", "6525", "7035", "3605", "6540", "6640", "7050", "7025", "7490", "7910", "2320", "7730", "5820", "7110", "9925", "6720", "7125", "5130", "6650", "7020", "5836", "7021" };

    std::string data = "";
    for (int i = 0; i < number_of_rows; i++) {
        data += device_code[random_index(sizeof(device_code), sizeof(device_code[0]))] + DELIMITER;
        data += "N" + DELIMITER; // centrally_managed_indicator
        data += "N" + DELIMITER; // retired_indicator
        data += device_description[random_index(sizeof(device_description), sizeof(device_description[0]))] + DELIMITER;
        data += risk_level_type_code[random_index(sizeof(risk_level_type_code), sizeof(risk_level_type_code[0]))] + DELIMITER;
        data += device_class_code[random_index(sizeof(device_class_code), sizeof(device_class_code[0]))] + DELIMITER;
        data += accountable_equipment_code[random_index(sizeof(accountable_equipment_code), sizeof(accountable_equipment_code[0]))] + DELIMITER;
        data += maintenance_required_indicator[random_index(sizeof(maintenance_required_indicator), sizeof(maintenance_required_indicator[0]))] + DELIMITER;
        data += life_expectancy[random_index(sizeof(life_expectancy), sizeof(life_expectancy[0]))] + DELIMITER;
        data += delete_indicator[random_index(sizeof(delete_indicator), sizeof(delete_indicator[0]))] + DELIMITER;
        data += specialty_code[random_index(sizeof(specialty_code), sizeof(specialty_code[0]))] + DELIMITER;
        data += federal_supply_class_fsc[random_index(sizeof(federal_supply_class_fsc), sizeof(federal_supply_class_fsc[0]))] + DELIMITER;
        data += '\n';
    }
    return data;
}

std::string generate_detm_filename(void) {
    // example: DETM-JWV16TEST10-1002-6-2021285.txt
    const std::string separator = "-";
    return "DETM" + separator + random_alphanumeric_string(11) + separator + random_number_string(4) + "-6-" + random_date_string() + ".txt";
}
