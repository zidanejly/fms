package com.jfhealthcare.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import springfox.documentation.spring.web.json.Json;

public class Test {
	public static void main(String[] args) throws IOException {
		double parseDouble = Double.parseDouble("0.228\\0.228".replace(',', '.'));
		System.out.println(parseDouble);
		/*try {
			RandomAccessFile ra = new RandomAccessFile(new File("D:\\aa4bcb2327b8a54e489d1055a09283dd735ab858.dcm"),"rw");
			FileOutputStream fo = new FileOutputStream(new File("D:\\1.dcm"));
			
			byte [] b = new byte[1024];
			int a =0;
			
			while ((a=ra.read(b))!=-1){
				fo.write(b, 0, a);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }
	private static void a(File f) {
		f = new File("D:\\fileSystemTest\\test.txt");
		System.out.println(f.toPath().toString() + "||");
	}

	class Student {
		public int age;
		public String name;

		public Student(int age, String name) {
			this.age = age;
			this.name = name;
		}
	}
}
