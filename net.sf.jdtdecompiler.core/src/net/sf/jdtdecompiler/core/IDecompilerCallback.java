package net.sf.jdtdecompiler.core;

/**
 * Indirect resolution of decompiler so that the source mapper can switch
 * decompilers on-the-fly without knowing this.
 * 
 * @author Johann Gyger
 */
public interface IDecompilerCallback {

    IDecompiler getDecompiler();

}
