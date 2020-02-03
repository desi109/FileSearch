package com.mdesislava.filesearch;


public class Main {
    public static void main(String[] args) {
        FileSearch searcher = new FileSearch();

        switch( Math.min(args.length, 3)) {
            case 0:
                System.out.println("USAGE: FileSearchApp path [regex] [zipfile]");
                return;
            case 3: searcher.setZipFileName(args[2]);
            case 2: searcher.setRegex(args[1]);
            case 1: searcher.setPath(args[0]);
        }
        try {
            searcher.walkDirectory(searcher.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
