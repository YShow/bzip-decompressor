package decompressor;

import static java.util.function.Predicate.not;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.bzip2.BZip2Utils;

public final class CompressorFinder {
	private static final Logger log = Logger.getLogger(CompressorFinder.class.toString());

	private static final Path COMPRESSEDFILESLOCATION = Path.of(System.getProperty("compressor","./"));
	private static final Path DECOMPRESSEDFILESLOCATION = Path.of(System.getProperty("decompressor","./"));

	//
	public static void main(final String[] args) {
		System.out.println("Path to compressed files: " + COMPRESSEDFILESLOCATION);
		System.out.println("Path to decompress files: " + DECOMPRESSEDFILESLOCATION);
		final var files = new HashMap<String, Path>();

		
		while(true)
		{
			checkNewItems(COMPRESSEDFILESLOCATION,files);
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				//SHOULD NEVER REACH HERE
				e.printStackTrace();
			}
			System.gc();
		}		
	}
	private static final void checkNewItems(final Path pathToItems, final Map<String, Path> files){
		try(final var file = Files.newDirectoryStream(pathToItems,"*.bz2"))
		{
			file.forEach(path -> computeNewFIle(files, path));			
		} catch (IOException e) {
			//SHOULD NEVER REACH HERE
			log.log(Level.SEVERE, () -> "Error searching files, message: " + e.getMessage());
		}				
	}

	private static final Path computeNewFIle(final Map<String, Path> files,final Path path) {
		return files.computeIfAbsent(BZip2Utils.getUncompressedFilename(path.getFileName().toString()), k -> {
			System.out.println("new file added to the map: " + k);
			try(final var fileStream = Files.list(DECOMPRESSEDFILESLOCATION)) {
				if(fileStream.noneMatch(e -> e.getFileName().toString().equals(k))) {
				DecompressorUtil.decompress(path, DECOMPRESSEDFILESLOCATION,k);
				}
			} catch (IOException e) {
				//SHOULD NEVER REACH HERE
				log.log(Level.SEVERE, () -> "Error walking files on: " + path);
			}
			return path;});
	}
	
	
	
}
