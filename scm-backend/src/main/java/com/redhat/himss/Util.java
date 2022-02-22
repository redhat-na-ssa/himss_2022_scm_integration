package com.redhat.himss;

public class Util {
    public final static String AM3X = "AM3X";
    public final static String DETM = "DETM";
    public final static String TXT = "txt";
    public static final String RESPONSE_HEADER="RESPONSE_HEADER";
    public static final String FILE_NAME_HEADER="FILE_NAME_HEADER";
    public static final String AM3X_INSERT_SQL_HEADER = "insert into AM3X (item_id, sub_organization_identifier_sub_org_id, assemblage_identifier, assemblage_increment, assemblage_sub_assemblage, assemblage_instance_number, stratification_state, on_hand_quantity, equipment_control_number, location, incomplete_flag_indicator, manufacturer_common_name, excess_quantity_remaining, expiration_date, lot_number, expiration_extension_date, manufacturer_name, product_number ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String DETM_INSERT_SQL_HEADER = "insert into DETM (device_code, centrally_managed_indicator, retired_indicator, device_description,risk_level_type_code, device_class_code, accountable_equipment_code, maintenance_required_indicator, life_expectancy,delete_indicator,specialty_code, federal_supply_class_fsc) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
}
