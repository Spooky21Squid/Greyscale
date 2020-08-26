package greyscale;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;

/**
 * Converts an image to a greyscale image by taking the average of the red, green and blue pixel values
 * and setting each r, g and b value to that average.
 * 
 * Usage:
 * 
 * @author Mathew Fitzgerald
 * 
 * Arguments:
 * 
 * 1: path to the image
 * 2: path and file name to save the new image to
 *
 */
public class Greyscale {

	public static void main(String[] args) {
		
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		}
		
		if (args.length == 1) {
			if (args[0] == "-h" || args[0] == "--help") {
				printUsage();
				System.exit(0);
			} else {
				printUsage();
				System.exit(1);
			}
		}
		
		if (args.length > 2) {
			printUsage();
			System.exit(1);
		}
		
		final String pathToRead = args[0];
		final String pathToWrite = args[1];
		
		File imageReadFile = new File(pathToRead);
		
		if (!imageReadFile.exists()) {
			System.err.println("File at '" + pathToRead + "' does not exist.");
			System.exit(1);
		} else if (!imageReadFile.canRead()) {
			System.err.println("File at '" + pathToRead + "' cannot be read.");
			System.exit(1);
		}
		
		BufferedImage image = null;
		
		try {
			image = Imaging.getBufferedImage(imageReadFile);
		} catch (ImageReadException e) {
			System.err.println("The file '" + pathToRead + "' can't be parsed. Try a different image format.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("An IOException occurred when parsing the Image.");
			System.exit(1);
		}
		
		convertToGreyscale(image);
		
		try {
			writeBufferedImage(image, pathToWrite);
		} catch (ImageWriteException e) {
			System.err.println("Encountered an ImageWriteExcepion while writing the image to '" + pathToWrite + "'.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Encountered an IOExcepion while writing the image to '" + pathToWrite + "'.");
			System.exit(1);
		}
		
		System.out.println("Converted to greyscale!");
	}
	
	/**
	 * Prints the usage message of this program to standard output
	 */
	public static void printUsage() {
		String usage = "usage: greyscale input_file output_file";
		
		System.err.println(usage);
	}
	
	/**
	 * Converts an image into a greyscale image by taking the average of the r, g and b values, and setting
	 * each value as that average.
	 * @param image The colour image to be converted
	 */
	public static void convertToGreyscale(BufferedImage image) {
		final int height = image.getHeight();
		final int width = image.getWidth();
		int rgb, red, green, blue, ave;
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				rgb = image.getRGB(x, y);
				red = (rgb >> 16) & 0xFF;
				green = (rgb >> 8) & 0xFF;
				blue = rgb & 0xFF;
				ave = (red + green + blue) / 3;
				//rgb = (ave << 16)|(ave << 8)|(ave);
				rgb = new Color(ave, ave, ave).getRGB();
				image.setRGB(x, y, rgb);
			}
		}
	}
	
	/**
	 * Writes a BufferedImage image to a file
	 * @param image The BufferedImage image to write
	 * @param path The path and file name the image should be written to
	 * @throws IOException 
	 * @throws ImageWriteException 
	 */
	public static void writeBufferedImage(BufferedImage image, String path) throws ImageWriteException, IOException {
		Map<String, Object> params = new HashMap<>();
		
		ImageFormats format = null;

		if (path.endsWith(".bmp")) {
			format = ImageFormats.BMP;
		} else if (path.endsWith(".ico")) {
			format = ImageFormats.ICO;
		} else if (path.endsWith(".gif")) {
			format = ImageFormats.GIF;
		} else if (path.endsWith(".pcx")) {
			format = ImageFormats.PCX;
		} else if (path.endsWith(".dcx")) {
			format = ImageFormats.DCX;
		} else if (path.endsWith(".png")) {
			format = ImageFormats.PNG;
		} else if (path.endsWith(".tiff")) {
			format = ImageFormats.TIFF;
		} else if (path.endsWith(".wbmp")) {
			format = ImageFormats.WBMP;
		} else if (path.endsWith(".xbm")) {
			format = ImageFormats.XBM;
		} else if (path.endsWith(".xpm")) {
			format = ImageFormats.XPM;
		} else {
			System.err.println("The format '" + path + "' is not supported. Try a different format.");
			System.exit(1);
		}
				
		byte[] bytes = Imaging.writeImageToBytes(image, format, params);
		
		File file = new File(path);
		if (file.exists()) {
			System.err.println("A file already exists at '" + path + "'.");
			System.exit(1);
		}
		
		FileUtils.writeByteArrayToFile(file, bytes);
	}
}
