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
}
