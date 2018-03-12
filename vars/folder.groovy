//package Energos.Jenkins.Common

import Jenkins.Common.Folder

def call(String path){
    new Folder(path)
}