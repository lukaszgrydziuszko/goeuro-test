package pl.grydzluk.goeuro

import java.nio.file.Paths

/**
 * Created by luk on 2016-08-21.
 */

static getFilePathFromResources(String fileName) {
    Paths.get(ClassLoader.getSystemResource(fileName).toURI()).toString()
}