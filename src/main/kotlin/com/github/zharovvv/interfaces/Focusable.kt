package com.github.zharovvv.interfaces

interface Focusable {

    fun setFocus(b: Boolean) {
        //do Nothing
    }

    fun showOff() = println("I`m focusable!")

    /**
     * Так будет выглядеть интерфейс при компиляции:
     */
//    public interface Focusable {
//   void setFocus(boolean var1);
//
//   void showOff();
//
//   public static final class DefaultImpls {
//      public static void setFocus(@NotNull Focusable $this, boolean b) {
//      }
//
//      public static void showOff(@NotNull Focusable $this) {
//         String var1 = "I`m focusable!";
//         boolean var2 = false;
//         System.out.println(var1);
//      }
//   }
//}
}