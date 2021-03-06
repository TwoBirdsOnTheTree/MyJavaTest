package com.mytest.nio;

import com.mytest.util.Util;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class Nio_Test {

    @Test
    void my_first_test_nio_read() throws Exception {
        String path = "C:\\Users\\Stark\\Desktop\\test.txt";
        try (FileChannel channel =
                     new RandomAccessFile(path, "rw").getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(10);

            while (channel.read(buffer) != -1) {
                //TODO 一定需要flip, 否则, 无法读取到
                buffer.flip();

                while (buffer.hasRemaining())
                    System.out.print((char) buffer.get());

                //TODO 不clear的话, 会死循环(无限读)
                // 因为get读取结束后, position == limit, 导致read无法再往buffer中保存数据了
                buffer.clear();
            }
        }
    }

    @Test
    void test_filechannel_read() throws Exception {
        try (FileChannel channel
                     = new RandomAccessFile(Util.path, "rw").getChannel()) {

            ByteBuffer b = ByteBuffer.allocate(1000);

            b.position(5);
            b.limit(10);

            channel.read(b);
            System.out.println("b pos: " + b.position() + ", b.lim: " + b.limit());

            // 显示全部内容
            b.position(0);
            b.limit(b.capacity());
            while (b.hasRemaining()) {
                char get = (char) b.get();
                //TODO char的默认值是 '\0'
                if ('\0' == get)
                    get = '_';
                System.out.print(get);
            }

            //TODO 结果说明, read往buffer写入, 是写入pos到lim这一段
        }
    }

    @Test
    void test_read_and_so_on() throws FileNotFoundException {
        String path = "C:\\Users\\Stark\\Desktop\\test.json";
        try (FileChannel channel = new RandomAccessFile(path, "rw").getChannel()) {
            System.out.println("channel size: " + channel.size());
            channel.position(channel.size());

            ByteBuffer buffer = ByteBuffer.allocate(40);
            System.out.println("初始化 position: " + buffer.position() + ", limit: " + buffer.limit());

            buffer.put(/*20 bytes*/"1234567890123456789012345678901234567890".getBytes());
            System.out.println("put后 position: " + buffer.position() + ", limit: " + buffer.limit());

            buffer.position(10);
            buffer.clear();
            buffer.position(buffer.limit());
            // System.out.println("clear后 position: " + buffer.position() + ", limit: " + buffer.limit());

            buffer.flip();
            System.out.println("flip后 position: " + buffer.position() + ", limit: " + buffer.limit());

            while (buffer.hasRemaining()) {
                channel.write(buffer);
                System.out.println("写一次后, buffer position: " + buffer.position() + ", limit: " + buffer.limit());
            }

            System.out.println("write结束, buffer position: " + buffer.position() + ", limit: " + buffer.limit());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_charbuffer_get_put() {
        CharBuffer buffer = CharBuffer.allocate(1024);
        buffer.put("我们");

        buffer.flip();

        System.out.println(buffer.get());
        System.out.println(buffer.get());
    }

    @Test
    void long_time_no_see_let_me_try() throws IOException {
        FileChannel fc = FileChannel.open(Path.of("C:\\Users\\sunch\\Desktop\\pt.txt"),
                StandardOpenOption.READ, StandardOpenOption.WRITE);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (fc.read(byteBuffer) != -1) {
            byteBuffer.flip();
            String read = new String(byteBuffer.array());
            System.out.println(read);
            byteBuffer.clear();
        }
    }

    @Test
    void let_me_try_append_file() {
        try (FileChannel fc = FileChannel.open(Path.of("C:\\Users\\sunch\\Desktop\\pt.txt"),
                StandardOpenOption.READ, StandardOpenOption.WRITE);) {

            fc.position(fc.size());

            fc.write(ByteBuffer.wrap("\nJustKeep".getBytes()));
            fc.force(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void let_me_try_easiest_way_to_copy_file() throws IOException {
        Path fromPath = Path.of("C:\\Users\\sunch\\Desktop\\pt.txt");
        Path toPath = Path.of("C:\\Users\\sunch\\Desktop\\copyTo.txt");

        if (!toPath.toFile().exists()) {
            toPath.toFile().createNewFile();
        }

        try (FileChannel from = FileChannel.open(fromPath,
                StandardOpenOption.READ, StandardOpenOption.WRITE);
             FileChannel to = FileChannel.open(toPath,
                     StandardOpenOption.READ, StandardOpenOption.WRITE)) {

            from.transferTo(0, from.size(), to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
