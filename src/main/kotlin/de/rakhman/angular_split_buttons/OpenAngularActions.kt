package de.rakhman.angular_split_buttons

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.psi.PsiFile
import icons.JavaScriptPsiIcons
import icons.SassIcons
import javax.swing.Icon
import javax.swing.JSplitPane

abstract class AbstractOpenAngularAction(
    title: String,
    icon: Icon,
) : AnAction({ title }, icon) {
    abstract val extensions: Map<String, Icon>

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val sibling = file?.let { getSibling(it) }

        e.presentation.isEnabledAndVisible =
            file != null && sibling != null && !hasMatchingExtension(file)
        e.presentation.text = "Open ${sibling?.name}"
        e.presentation.icon = sibling?.name?.substringAfterLast(".")?.let {
            extensions[it.lowercase()]
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val myFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val myEditor = e.getData(CommonDataKeys.EDITOR) ?: return
        val siblingFile = getSibling(myFile)?.virtualFile ?: return

        val fileEditorManager = FileEditorManagerEx.getInstanceEx(myFile.project)

        // find existing window where the sibling file is opened and which is not the window
        // where the action was performed
        val window = fileEditorManager.splitters.windows.firstOrNull {
            !it.hasEditor(myEditor) && it.hasFile(siblingFile)
        }

        if (window != null) {
            // if it exists, open the respective editor and focus it
            window.setSelectedComposite(window.allComposites.first { it.file == siblingFile }, true)
        } else {
            // otherwise, split the current window
            val currentWindow =
                fileEditorManager.splitters.windows.firstOrNull { it.hasEditor(myEditor) }
                    ?: fileEditorManager.currentWindow

            currentWindow.split(
                JSplitPane.HORIZONTAL_SPLIT,
                false,
                siblingFile,
                true,
            )
        }
    }

    private fun getSibling(file: PsiFile): PsiFile? {
        val name = file.name.substringBeforeLast(".")
        val siblings = file.parent?.files ?: return null

        return siblings.firstOrNull {
            it.name.substringBeforeLast(".") == name && hasMatchingExtension(it)
        }
    }

    private fun hasMatchingExtension(file: PsiFile): Boolean {
        return file.name.substringAfterLast(".").lowercase() in extensions
    }
}


class OpenAngularTemplateAction :
    AbstractOpenAngularAction("Open Component Template", AllIcons.FileTypes.Html) {
    override val extensions = mapOf(
        "html" to AllIcons.FileTypes.Html,
    )
}

class OpenAngularCodeAction :
    AbstractOpenAngularAction("Open Component Code", JavaScriptPsiIcons.FileTypes.TypeScriptFile) {
    override val extensions = mapOf(
        "ts" to JavaScriptPsiIcons.FileTypes.TypeScriptFile,
    )
}

class OpenAngularStylesAction :
    AbstractOpenAngularAction("Open Component Styles", AllIcons.FileTypes.Css) {
    override val extensions = mapOf(
        "css" to AllIcons.FileTypes.Css,
        "scss" to SassIcons.Sass,
    )
}
