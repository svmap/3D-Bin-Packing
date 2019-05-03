package packing.payload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CSV2JSON 
{
	@Test
	public List<String> csvtojson(String fileInput) throws Exception 
	{
	    CsvSchema csv = CsvSchema.emptySchema().withHeader();
		CsvMapper csvMapper = new CsvMapper();
		MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader().forType(Map.class).with(csv).readValues(fileInput);
		List ListofKeys = new ArrayList(mappingIterator.readAll());
		System.out.println(ListofKeys);
		return ListofKeys;
		//System.out.println(list);
	}

	
}
