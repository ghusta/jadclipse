package net.sf.jdtdecompiler.ui;

import net.sf.jdtdecompiler.core.DecompilerType;
import net.sf.jdtdecompiler.core.IDecompiler;

import org.eclipse.jface.action.Action;

class DecompileWithAction extends Action {

    private final DecompiledClassFileEditor editor;
    
    private final DecompilerType decompilerType;

    public DecompileWithAction(DecompiledClassFileEditor editor, DecompilerType decompilerType) {
        super(decompilerType.getName());
        this.editor = editor;
        this.decompilerType = decompilerType;
    }

    @Override
    public void run() {
        if (editor != null) {
            IDecompiler decompiler = decompilerType.getDecompiler();
            editor.decompileWith(decompiler);
        }
    }
}
