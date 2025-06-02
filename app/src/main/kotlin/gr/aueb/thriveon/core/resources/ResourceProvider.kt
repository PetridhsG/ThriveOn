package gr.aueb.thriveon.core.resources

import android.content.Context
import androidx.annotation.StringRes

interface ResourceProvider {
    fun provideContext(): Context

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

class ResourceProviderImpl(
    private val context: Context,
) : ResourceProvider {
    override fun provideContext() = context

    override fun getString(resId: Int, vararg formatArgs: Any): String =
        try {
            context.getString(resId, *formatArgs)
        } catch (_: Exception) {
            ""
        }
}
