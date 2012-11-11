package com.tjeannin.provigen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

import com.tjeannin.provigen.annotations.Column;
import com.tjeannin.provigen.annotations.ContentUri;
import com.tjeannin.provigen.annotations.Id;

public class ContractHolder {

	private String authority;
	private String idField;
	private String tableName;
	private List<DatabaseField> databaseFields;

	@SuppressWarnings("rawtypes")
	public ContractHolder(Class contractClass) throws InvalidContractException {

		databaseFields = new ArrayList<DatabaseField>();

		Field[] fields = contractClass.getFields();
		for (Field field : fields) {

			ContentUri contentUri = field.getAnnotation(ContentUri.class);
			if (contentUri != null) {
				if (authority != null || tableName != null) {
					throw new InvalidContractException("A contract can not have several @ContentUri.");
				}
				try {
					Uri uri = (Uri) field.get(null);
					authority = uri.getAuthority();
					tableName = uri.getLastPathSegment();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				if (idField != null) {
					throw new InvalidContractException("A contract can not have several fields annoted with Id.");
				}
				try {
					idField = (String) field.get(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				try {
					databaseFields.add(new DatabaseField((String) field.get(null), column.type()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (authority == null || tableName == null) {
			throw new InvalidContractException("The contract is missing a content uri.");
		}
	}

	public String getAuthority() {
		return authority;
	}

	public String getIdField() {
		return idField;
	}

	public String getTable() {
		return tableName;
	}

	public List<DatabaseField> getFields() {
		return databaseFields;
	}
}