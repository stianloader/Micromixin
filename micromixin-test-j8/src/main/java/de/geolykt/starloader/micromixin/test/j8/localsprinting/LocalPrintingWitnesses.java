package de.geolykt.starloader.micromixin.test.j8.localsprinting;

public class LocalPrintingWitnesses {
    public static final String[] LOCAL_PRINTING_TEST_RETURN_LOCAL_STATIC_0 = new String[] {
            "/****************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                  */",
            "/*      Target Method : public int returnLocalStatic0()                                                                     */",
            "/*  Target Max LOCALS : 4                                                                                                   */",
            "/* Initial Frame Size : 0                                                                                                   */",
            "/*      Callback Name : injectorLocalStatic0                                                                                */",
            "/*        Instruction : InjectionNode IRETURN                                                                               */",
            "/****************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                      */",
            "/* > [  0]                   int  local0                                             <capture>                              */",
            "/*   [  1]                   int  local1                                             <capture>                              */",
            "/*   [  2]                   int  local2                                             <capture>                              */",
            "/*   [  3]                   int  local3                                             <capture>                              */",
            "/****************************************************************************************************************************/",
            "/*                                                                                                                          */",
            "/* /**                                                                                                                      */",
            "/*  * Expected callback signature                                                                                           */",
            "/*  * /                                                                                                                     */",
            "/* private void injectorLocalStatic0(CallbackInfoReturnable<Integer> cir, int local0, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                       */",
            "/* }                                                                                                                        */",
            "/*                                                                                                                          */",
            "/****************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_0_B = new String[] {
            "/******************************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                                */",
            "/*      Target Method : public int returnLocalStaticArg0(int arg0)                                                                        */",
            "/*  Target Max LOCALS : 5                                                                                                                 */",
            "/* Initial Frame Size : 1                                                                                                                 */",
            "/*      Callback Name : injectorLocalStaticArg0B                                                                                          */",
            "/*        Instruction : InjectionNode IRETURN                                                                                             */",
            "/******************************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                                    */",
            "/*   [  0]                   int  arg0                                                                                                    */",
            "/* > [  1]                   int  local0                                             <capture>                                            */",
            "/*   [  2]                   int  local1                                             <capture>                                            */",
            "/*   [  3]                   int  local2                                             <capture>                                            */",
            "/*   [  4]                   int  local3                                             <capture>                                            */",
            "/******************************************************************************************************************************************/",
            "/*                                                                                                                                        */",
            "/* /**                                                                                                                                    */",
            "/*  * Expected callback signature                                                                                                         */",
            "/*  * /                                                                                                                                   */",
            "/* private void injectorLocalStaticArg0A(int arg0, CallbackInfoReturnable<Integer> cir, int local0, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                                     */",
            "/* }                                                                                                                                      */",
            "/*                                                                                                                                        */",
            "/******************************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_0_A = new String[] {
            "/******************************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                                */",
            "/*      Target Method : public int returnLocalStaticArg0(int arg0)                                                                        */",
            "/*  Target Max LOCALS : 5                                                                                                                 */",
            "/* Initial Frame Size : 1                                                                                                                 */",
            "/*      Callback Name : injectorLocalStaticArg0A                                                                                          */",
            "/*        Instruction : InjectionNode IRETURN                                                                                             */",
            "/******************************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                                    */",
            "/*   [  0]                   int  arg0                                                                                                    */",
            "/* > [  1]                   int  local0                                             <capture>                                            */",
            "/*   [  2]                   int  local1                                             <capture>                                            */",
            "/*   [  3]                   int  local2                                             <capture>                                            */",
            "/*   [  4]                   int  local3                                             <capture>                                            */",
            "/******************************************************************************************************************************************/",
            "/*                                                                                                                                        */",
            "/* /**                                                                                                                                    */",
            "/*  * Expected callback signature                                                                                                         */",
            "/*  * /                                                                                                                                   */",
            "/* private void injectorLocalStaticArg0A(int arg0, CallbackInfoReturnable<Integer> cir, int local0, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                                     */",
            "/* }                                                                                                                                      */",
            "/*                                                                                                                                        */",
            "/******************************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_LOCAL_INSTANCE_0 = new String[] {
            "/****************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                  */",
            "/*      Target Method : public int returnLocalInstance0()                                                                   */",
            "/*  Target Max LOCALS : 4                                                                                                   */",
            "/* Initial Frame Size : 1                                                                                                   */",
            "/*      Callback Name : injectorLocalInstance0                                                                              */",
            "/*        Instruction : InjectionNode IRETURN                                                                               */",
            "/****************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                      */",
            "/*   [  0]     LocalPrintingTest  this                                                                                      */",
            "/* > [  1]                   int  local1                                             <capture>                              */",
            "/*   [  2]                   int  local2                                             <capture>                              */",
            "/*   [  3]                   int  local3                                             <capture>                              */",
            "/****************************************************************************************************************************/",
            "/*                                                                                                                          */",
            "/* /**                                                                                                                      */",
            "/*  * Expected callback signature                                                                                           */",
            "/*  * /                                                                                                                     */",
            "/* private void injectorLocalInstance0(CallbackInfoReturnable<Integer> cir, int local1, int local2, int local3) {           */",
            "/*     // Method body                                                                                                       */",
            "/* }                                                                                                                        */",
            "/*                                                                                                                          */",
            "/****************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_1_A = new String[] {
            "/*********************************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                                   */",
            "/*      Target Method : public int returnLocalStaticArg1(Object arg0)                                                                        */",
            "/*  Target Max LOCALS : 5                                                                                                                    */",
            "/* Initial Frame Size : 1                                                                                                                    */",
            "/*      Callback Name : injectorLocalStaticArg1A                                                                                             */",
            "/*        Instruction : InjectionNode IRETURN                                                                                                */",
            "/*********************************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                                       */",
            "/*   [  0]                Object  arg0                                                                                                       */",
            "/* > [  1]                   int  local0                                             <capture>                                               */",
            "/*   [  2]                   int  local1                                             <capture>                                               */",
            "/*   [  3]                   int  local2                                             <capture>                                               */",
            "/*   [  4]                   int  local3                                             <capture>                                               */",
            "/*********************************************************************************************************************************************/",
            "/*                                                                                                                                           */",
            "/* /**                                                                                                                                       */",
            "/*  * Expected callback signature                                                                                                            */",
            "/*  * /                                                                                                                                      */",
            "/* private void injectorLocalStaticArg1A(Object arg0, CallbackInfoReturnable<Integer> cir, int local0, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                                        */",
            "/* }                                                                                                                                         */",
            "/*                                                                                                                                           */",
            "/*********************************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_1_B = new String[] {
            "/*********************************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                                   */",
            "/*      Target Method : public int returnLocalStaticArg1(Object arg0)                                                                        */",
            "/*  Target Max LOCALS : 5                                                                                                                    */",
            "/* Initial Frame Size : 1                                                                                                                    */",
            "/*      Callback Name : injectorLocalStaticArg1B                                                                                             */",
            "/*        Instruction : InjectionNode IRETURN                                                                                                */",
            "/*********************************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                                       */",
            "/*   [  0]                Object  arg0                                                                                                       */",
            "/* > [  1]                   int  local0                                             <capture>                                               */",
            "/*   [  2]                   int  local1                                             <capture>                                               */",
            "/*   [  3]                   int  local2                                             <capture>                                               */",
            "/*   [  4]                   int  local3                                             <capture>                                               */",
            "/*********************************************************************************************************************************************/",
            "/*                                                                                                                                           */",
            "/* /**                                                                                                                                       */",
            "/*  * Expected callback signature                                                                                                            */",
            "/*  * /                                                                                                                                      */",
            "/* private void injectorLocalStaticArg1B(Object arg0, CallbackInfoReturnable<Integer> cir, int local0, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                                        */",
            "/* }                                                                                                                                         */",
            "/*                                                                                                                                           */",
            "/*********************************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_0_A = new String[] {
            "/********************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                      */",
            "/*      Target Method : public int returnLocalInstanceArg0(int arg0)                                                            */",
            "/*  Target Max LOCALS : 5                                                                                                       */",
            "/* Initial Frame Size : 2                                                                                                       */",
            "/*      Callback Name : injectorLocalInstanceArg0A                                                                              */",
            "/*        Instruction : InjectionNode IRETURN                                                                                   */",
            "/********************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                          */",
            "/*   [  0]     LocalPrintingTest  this                                                                                          */",
            "/*   [  1]                   int  arg0                                                                                          */",
            "/* > [  2]                   int  local1                                             <capture>                                  */",
            "/*   [  3]                   int  local2                                             <capture>                                  */",
            "/*   [  4]                   int  local3                                             <capture>                                  */",
            "/********************************************************************************************************************************/",
            "/*                                                                                                                              */",
            "/* /**                                                                                                                          */",
            "/*  * Expected callback signature                                                                                               */",
            "/*  * /                                                                                                                         */",
            "/* private void injectorLocalInstanceArg0A(int arg0, CallbackInfoReturnable<Integer> cir, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                           */",
            "/* }                                                                                                                            */",
            "/*                                                                                                                              */",
            "/********************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_0_B = new String[] {
            "/********************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                      */",
            "/*      Target Method : public int returnLocalInstanceArg0(int arg0)                                                            */",
            "/*  Target Max LOCALS : 5                                                                                                       */",
            "/* Initial Frame Size : 2                                                                                                       */",
            "/*      Callback Name : injectorLocalInstanceArg0B                                                                              */",
            "/*        Instruction : InjectionNode IRETURN                                                                                   */",
            "/********************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                          */",
            "/*   [  0]     LocalPrintingTest  this                                                                                          */",
            "/*   [  1]                   int  arg0                                                                                          */",
            "/* > [  2]                   int  local1                                             <capture>                                  */",
            "/*   [  3]                   int  local2                                             <capture>                                  */",
            "/*   [  4]                   int  local3                                             <capture>                                  */",
            "/********************************************************************************************************************************/",
            "/*                                                                                                                              */",
            "/* /**                                                                                                                          */",
            "/*  * Expected callback signature                                                                                               */",
            "/*  * /                                                                                                                         */",
            "/* private void injectorLocalInstanceArg0B(int arg0, CallbackInfoReturnable<Integer> cir, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                           */",
            "/* }                                                                                                                            */",
            "/*                                                                                                                              */",
            "/********************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_1_A = new String[] {
            "/***********************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                         */",
            "/*      Target Method : public int returnLocalInstanceArg1(Object arg0)                                                            */",
            "/*  Target Max LOCALS : 5                                                                                                          */",
            "/* Initial Frame Size : 2                                                                                                          */",
            "/*      Callback Name : injectorLocalInstanceArg1A                                                                                 */",
            "/*        Instruction : InjectionNode IRETURN                                                                                      */",
            "/***********************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                             */",
            "/*   [  0]     LocalPrintingTest  this                                                                                             */",
            "/*   [  1]                Object  arg0                                                                                             */",
            "/* > [  2]                   int  local1                                             <capture>                                     */",
            "/*   [  3]                   int  local2                                             <capture>                                     */",
            "/*   [  4]                   int  local3                                             <capture>                                     */",
            "/***********************************************************************************************************************************/",
            "/*                                                                                                                                 */",
            "/* /**                                                                                                                             */",
            "/*  * Expected callback signature                                                                                                  */",
            "/*  * /                                                                                                                            */",
            "/* private void injectorLocalInstanceArg1A(Object arg0, CallbackInfoReturnable<Integer> cir, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                              */",
            "/* }                                                                                                                               */",
            "/*                                                                                                                                 */",
            "/***********************************************************************************************************************************/"
    };

    public static final String[] LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_1_B = new String[] {
            "/***********************************************************************************************************************************/",
            "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                         */",
            "/*      Target Method : public int returnLocalInstanceArg1(Object arg0)                                                            */",
            "/*  Target Max LOCALS : 5                                                                                                          */",
            "/* Initial Frame Size : 2                                                                                                          */",
            "/*      Callback Name : injectorLocalInstanceArg1B                                                                                 */",
            "/*        Instruction : InjectionNode IRETURN                                                                                      */",
            "/***********************************************************************************************************************************/",
            "/*   LOCAL                  TYPE  NAME                                                                                             */",
            "/*   [  0]     LocalPrintingTest  this                                                                                             */",
            "/*   [  1]                Object  arg0                                                                                             */",
            "/* > [  2]                   int  local1                                             <capture>                                     */",
            "/*   [  3]                   int  local2                                             <capture>                                     */",
            "/*   [  4]                   int  local3                                             <capture>                                     */",
            "/***********************************************************************************************************************************/",
            "/*                                                                                                                                 */",
            "/* /**                                                                                                                             */",
            "/*  * Expected callback signature                                                                                                  */",
            "/*  * /                                                                                                                            */",
            "/* private void injectorLocalInstanceArg1B(Object arg0, CallbackInfoReturnable<Integer> cir, int local1, int local2, int local3) { */",
            "/*     // Method body                                                                                                              */",
            "/* }                                                                                                                               */",
            "/*                                                                                                                                 */",
            "/***********************************************************************************************************************************/"
    };

    public static void main(String[] args) {
        String text = "\n"
                + "/***********************************************************************************************************************************/\n"
                + "/*       Target Class : de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest                                         */\n"
                + "/*      Target Method : public int returnLocalInstanceArg1(Object arg0)                                                            */\n"
                + "/*  Target Max LOCALS : 5                                                                                                          */\n"
                + "/* Initial Frame Size : 2                                                                                                          */\n"
                + "/*      Callback Name : injectorLocalInstanceArg1A                                                                                 */\n"
                + "/*        Instruction : InjectionNode IRETURN                                                                                      */\n"
                + "/***********************************************************************************************************************************/\n"
                + "/*   LOCAL                  TYPE  NAME                                                                                             */\n"
                + "/*   [  0]     LocalPrintingTest  this                                                                                             */\n"
                + "/*   [  1]                Object  arg0                                                                                             */\n"
                + "/* > [  2]                   int  local1                                             <capture>                                     */\n"
                + "/*   [  3]                   int  local2                                             <capture>                                     */\n"
                + "/*   [  4]                   int  local3                                             <capture>                                     */\n"
                + "/***********************************************************************************************************************************/\n"
                + "/*                                                                                                                                 */\n"
                + "/* /**                                                                                                                             */\n"
                + "/*  * Expected callback signature                                                                                                  */\n"
                + "/*  * /                                                                                                                            */\n"
                + "/* private void injectorLocalInstanceArg1A(Object arg0, CallbackInfoReturnable<Integer> cir, int local1, int local2, int local3) { */\n"
                + "/*     // Method body                                                                                                              */\n"
                + "/* }                                                                                                                               */\n"
                + "/*                                                                                                                                 */\n"
                + "/***********************************************************************************************************************************/";
        String[] split = text.split("[\\r\\n]");
        StringBuilder b = new StringBuilder();
        for (String s : split) {
            b.append("\"").append(s).append("\",\n");
        }
        b.setLength(b.length() - 2);
        System.out.println(b.toString());
    }
}
