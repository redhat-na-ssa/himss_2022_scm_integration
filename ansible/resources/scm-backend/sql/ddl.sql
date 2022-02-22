create table AM3X (
	id SERIAL,
	item_id varchar(32),
	sub_organization_identifier_sub_org_id varchar(6),
	assemblage_identifier varchar(4),
	assemblage_increment varchar(2),
	assemblage_sub_assemblage varchar(2),
	assemblage_instance_number int4,
	stratification_state varchar(3),
	on_hand_quantity int4,
	equipment_control_number int4,
	location varchar(32),
	incomplete_flag_indicator char(1),
	manufacturer_common_name varchar(256),
	excess_quantity_remaining int4,
	expiration_date date,
	lot_number varchar(32),
	expiration_extension_date date,
	manufacturer_name varchar(32),
	product_number int4,
	primary key (id)
);

create table DETM (
	id SERIAL,
	device_code varchar(5),
	centrally_managed_indicator char(1),
	retired_indicator char(1),
	device_description varchar(256),
	risk_level_type_code int4,
	device_class_code varchar(5),
	accountable_equipment_code char(1),
	maintenance_required_indicator char(1),
	life_expectancy int4,
	delete_indicator char(1),
	specialty_code varchar(3),
	federal_supply_class_fsc int4,
	primary key (id)
);
