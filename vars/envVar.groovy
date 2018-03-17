def call(String envKey, boolean withAssert = true){

    def res = envVar[envKey]
    if (withAssert)
        assert res != null

    res

}