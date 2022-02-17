package de.rakhman.angular_split_buttons

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.vfs.VirtualFile

fun EditorWindow.hasEditor(editor: Editor): Boolean {
    return selectedComposite?.allEditors?.filterIsInstance<TextEditor>()?.any { it.editor == editor } ?: false
}

fun EditorWindow.hasFile(siblingFile: VirtualFile): Boolean {
    return allComposites.any { it.file == siblingFile }
}