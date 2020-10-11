# bzip-decompressor
To change default location to compress and uncompress use

compress: -Dcompressor="/another/path"

decompress: -Ddecompressor="/another/pathToDecompress"

Can be compiled to native-image with this command line:
```
./native-image --no-fallback --allow-incomplete-classpath -cp commons-compress-1.20.jar -jar bzip-decompressor-0.0.1-SNAPSHOT.jar -H:Name=BZIPDecomp
```
