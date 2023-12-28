package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {
    private static Charset codingCP1250 = Charset.forName("Cp1250");
    private static Charset codingUTF8 = Charset.forName("UTF-8");

    public static void processDir(String dirName, String resultFileName){
        Path dirNamePath = Paths.get(dirName);

        try {
            Files.walkFileTree(dirNamePath, getSimpleFileVisitor(codingCP1250, codingUTF8, resultFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static SimpleFileVisitor getSimpleFileVisitor(Charset codingCP1250, Charset codingUTF8, String resultFileName){
        return new SimpleFileVisitor<Path>(){
            Path resultFileNamePath = Paths.get(resultFileName);

            FileChannel fileChannelOut;
            {
                try {
                    fileChannelOut = FileChannel.open(resultFileNamePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes) {

                try {
                    FileChannel fileChannel =  FileChannel.open(filePath);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(256);

                    byteBuffer.clear();
                    fileChannel.read(byteBuffer);
                    byteBuffer.flip();
                    CharBuffer charBuffer = codingCP1250.decode(byteBuffer);
                    ByteBuffer bufferLast = codingUTF8.encode(charBuffer);

                    if (bufferLast.hasRemaining()){
                        fileChannelOut.write(bufferLast);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return FileVisitResult.CONTINUE;
            }
        };
    }

}
