// FIX: Remove 'val' from parameter
class UsedInInitializer(private <caret>val x: Int) {
    var y: String

    init {
        y = x.toString()
    }
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.inspections.CanBeParameterInspection$RemoveValVarFix