package decompressor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.bzip2.BZip2Utils;

public final class CompressorFinder {
	public static final Logger LOGGER = Logger.getLogger(CompressorFinder.class.getSimpleName());

	private static final Path COMPRESSEDFILESLOCATION = Path.of(System.getProperty("compressor", "./"));
	private static final Path DECOMPRESSEDFILESLOCATION = Path.of(System.getProperty("decompressor", "./"));

	//
	public static void main(final String[] args) {
		LOGGER.log(Level.INFO,"Path to compressed files: " + COMPRESSEDFILESLOCATION);
		LOGGER.log(Level.INFO,"Path to decompress files: " + DECOMPRESSEDFILESLOCATION);
		checkNewItems(COMPRESSEDFILESLOCATION);
		LOGGER.log(Level.WARNING, "Done decompressing files");
		
	}

	private static final void checkNewItems(final Path pathToItems) {
		try (final var file = Files.newDirectoryStream(pathToItems, "*.bz2")) {
			file.forEach(path -> computeNewFile(path));
		} catch (IOException e) {
			// SHOULD NEVER REACH HERE
			LOGGER.log(Level.SEVERE, e, e::getMessage);
		}
	}

	private static final void computeNewFile(final Path path) {
		final var filename = BZip2Utils.getUncompressedFilename(path.getFileName().toString());
		if (Files.notExists(DECOMPRESSEDFILESLOCATION.resolve(filename))) {
			DecompressorUtil.decompress(path, DECOMPRESSEDFILESLOCATION, filename);
		}
	}

}
