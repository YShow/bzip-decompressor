package decompressor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public final class DecompressorUtil {
	private DecompressorUtil() {
	}

	public static final void decompress(final Path fileToDecompress, final Path pathToDecompressTo,
			final String filename) {
		final Supplier<String> message = () -> "Path cannot be null";
		Objects.requireNonNull(fileToDecompress, message);
		Objects.requireNonNull(pathToDecompressTo, message);
		Objects.requireNonNull(filename, message);

		final Path finalPath = pathToDecompressTo.resolve(filename);
		try (final var is = new BufferedInputStream(Files.newInputStream(fileToDecompress));
				final var in = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.BZIP2,
						is);
				final var out = new BufferedOutputStream(Files.newOutputStream(finalPath));) {
			in.transferTo(out);

			CompressorFinder.LOGGER.log(Level.INFO, () -> "File: " + filename + " was extracted to: " + pathToDecompressTo);
			
		} catch (IOException | CompressorException e) {
			CompressorFinder.LOGGER.log(Level.SEVERE, e, e::getMessage);
		}
	}
}
