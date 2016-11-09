package net.thh.geo.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.geometry.BoundingBox;

import com.csvreader.CsvWriter;

public class Shp2Csv {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Options options = new Options();
		options.addOption("i", "input", true, "input path");
		options.addOption("o", "output", true, "output path");
		try {

			CommandLineParser parser = new DefaultParser();

			CommandLine cl = parser.parse(options, args);

			File inputFile = new File(cl.getOptionValue("i"));
			File outputFile = new File(cl.getOptionValue("o", null));
			SimpleFeatureSource featureSource = null;
			if (StringUtils.endsWithIgnoreCase(inputFile.getName(), ".shp")) {
				ShapefileDataStore store = new ShapefileDataStore(inputFile.toURI().toURL());
				store.setCharset(Charsets.toCharset("MS932"));
				featureSource = store.getFeatureSource();
			}

			if (featureSource != null) {
				final SimpleFeatureType schema = featureSource.getSchema();
				final int attrCount = schema.getAttributeCount();
				final String[] values;
				{
					List<String> headers = new ArrayList<>();
					headers.add("minX");
					headers.add("minY");
					headers.add("maxX");
					headers.add("maxY");

					for (int i = 1; i < attrCount; i++) {
						AttributeType type = schema.getType(i);
						headers.add(type.getName().toString());
					}
					values = headers.toArray(new String[0]);
				}

				FileWriter fw = new FileWriter(outputFile, false);
				BufferedWriter writer = new BufferedWriter(fw);
				CsvWriter w = new CsvWriter(writer, ',');
				try {
					w.writeRecord(values);// Write Headers

					SimpleFeatureIterator ite = featureSource.getFeatures().features();
					DecimalFormat llformat = new DecimalFormat("#.0000000");
					while (ite.hasNext()) {
						SimpleFeature sf = ite.next();
						BoundingBox bb = sf.getDefaultGeometryProperty().getBounds();
						values[0] = llformat.format(bb.getMinX());
						values[1] = llformat.format(bb.getMinY());
						values[2] = llformat.format(bb.getMinX());
						values[3] = llformat.format(bb.getMinY());

						for (int i = 1; i < attrCount; i++) {
							values[i + 3] = sf.getAttribute(i).toString();
						}
						w.writeRecord(values);
					}
					w.endRecord();
				} finally {
					IOUtils.closeQuietly(writer);
					IOUtils.closeQuietly(fw);
				}
			}

		} catch (Throwable t) {
			t.printStackTrace();

		}
	}
}
