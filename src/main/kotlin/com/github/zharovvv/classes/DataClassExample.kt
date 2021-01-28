package com.github.zharovvv.classes

data class DataClassExample(
    val one: String,
    val two: String,
    val three: String
) {
    /**
     * свойство, объявленное не в первичном конструкторе, не участвует в генерируемых методах
     * (hashCode, equals, toString и copy).
     */
    var four: String = "unknown"
}

//Java:
//public final class DataClassExample {
//    @NotNull
//    private String four;
//    @NotNull
//    private final String one;
//    @NotNull
//    private final String two;
//    @NotNull
//    private final String three;
//
//    @NotNull
//    public final String getFour() {
//        return this.four;
//    }
//
//    public final void setFour(@NotNull String var1) {
//        Intrinsics.checkNotNullParameter(var1, "<set-?>");
//        this.four = var1;
//    }
//
//    @NotNull
//    public final String getOne() {
//        return this.one;
//    }
//
//    @NotNull
//    public final String getTwo() {
//        return this.two;
//    }
//
//    @NotNull
//    public final String getThree() {
//        return this.three;
//    }
//
//    public DataClassExample(@NotNull String one, @NotNull String two, @NotNull String three) {
//        Intrinsics.checkNotNullParameter(one, "one");
//        Intrinsics.checkNotNullParameter(two, "two");
//        Intrinsics.checkNotNullParameter(three, "three");
//        super();
//        this.one = one;
//        this.two = two;
//        this.three = three;
//        this.four = "unknown";
//    }
//
//    @NotNull
//    public final String component1() {
//        return this.one;
//    }
//
//    @NotNull
//    public final String component2() {
//        return this.two;
//    }
//
//    @NotNull
//    public final String component3() {
//        return this.three;
//    }
//
//    @NotNull
//    public final DataClassExample copy(@NotNull String one, @NotNull String two, @NotNull String three) {
//        Intrinsics.checkNotNullParameter(one, "one");
//        Intrinsics.checkNotNullParameter(two, "two");
//        Intrinsics.checkNotNullParameter(three, "three");
//        return new DataClassExample(one, two, three);
//    }
//
//    // $FF: synthetic method
//    public static DataClassExample copy$default(DataClassExample var0, String var1, String var2, String var3, int var4, Object var5) {
//        if ((var4 & 1) != 0) {
//            var1 = var0.one;
//        }
//
//        if ((var4 & 2) != 0) {
//            var2 = var0.two;
//        }
//
//        if ((var4 & 4) != 0) {
//            var3 = var0.three;
//        }
//
//        return var0.copy(var1, var2, var3);
//    }
//
//    @NotNull
//    public String toString() {
//        return "DataClassExample(one=" + this.one + ", two=" + this.two + ", three=" + this.three + ")";
//    }
//
//    public int hashCode() {
//        String var10000 = this.one;
//        int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
//        String var10001 = this.two;
//        var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
//        var10001 = this.three;
//        return var1 + (var10001 != null ? var10001.hashCode() : 0);
//    }
//
//    public boolean equals(@Nullable Object var1) {
//        if (this != var1) {
//            if (var1 instanceof DataClassExample) {
//                DataClassExample var2 = (DataClassExample)var1;
//                if (Intrinsics.areEqual(this.one, var2.one) && Intrinsics.areEqual(this.two, var2.two) && Intrinsics.areEqual(this.three, var2.three)) {
//                    return true;
//                }
//            }
//
//            return false;
//        } else {
//            return true;
//        }
//    }
//}