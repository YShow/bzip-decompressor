package decompressor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.bzip2.BZip2Utils;

public final class CompressorFinder {
	public static final Logger LOGGER = Logger.getLogger("Compressor");

	private static final Path COMPRESSEDFILESLOCATION = Path.of(System.getProperty("compressor","./"));
	private static final Path DECOMPRESSEDFILESLOCATION = Path.of(System.getProperty("decompressor","./"));

	//
	public static void main(final String[] args) {
		System.out.println("Path to compressed files: " + COMPRESSEDFILESLOCATION);
		System.out.println("Path to decompress files: " + DECOMPRESSEDFILESLOCATION);
		
		final var files = new HashSet<Path>();
		
		while(true)
		{
			checkNewItems(COMPRESSEDFILESLOCATION,files);
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				//SHOULD NEVER REACH HERE
				LOGGER.severe(() -> "Error on thread " + Arrays.toString(e.getStackTrace()));
			}
			System.gc();
		}		
	}
	private static final void checkNewItems(final Path pathToItems, final Set<Path> files){
		try(final var file = Files.newDirectoryStream(pathToItems,"*.bz2"))
		{
			file.forEach(path -> computeNewFile(files, path));			
		} catch (IOException e) {
			//SHOULD NEVER REACH HERE
			LOGGER.severe(() -> "Error searching files, message: " + Arrays.toString(e.getStackTrace()));
		}				
	}	
	private static final void computeNewFile(final Set<Path> files,final Path path) {		
		if(files.add(path))
		{
			final var filename = BZip2Utils.getUncompressedFilename(path.getFileName().toString());
			System.out.println("New file added to the list: " + filename);			
			try(final var fileStream = Files.list(DECOMPRESSEDFILESLOCATION)) {
				if(fileStream.noneMatch(e -> e.getFileName().toString().equals(filename))) {
				DecompressorUtil.decompress(path, DECOMPRESSEDFILESLOCATION,filename);
				}
			} catch (IOException e) {
				//SHOULD NEVER REACH HERE
				LOGGER.severe(() -> "Error walking files on: " + path + " Message: " + Arrays.toString(e.getStackTrace()));
			}
		}
	}
	
}
