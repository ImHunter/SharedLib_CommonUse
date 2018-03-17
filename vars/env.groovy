def call(String envKey, boolean withAssert = true){

    def res = env[envKey]
    if (withAssert)
        assert res != null

    res

}