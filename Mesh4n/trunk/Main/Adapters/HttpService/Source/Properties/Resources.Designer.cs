﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.1434
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Mesh4n.Adapters.HttpService.Properties
{


	/// <summary>
    ///   A strongly-typed resource class, for looking up localized strings, etc.
    /// </summary>
    // This class was auto-generated by the StronglyTypedResourceBuilder
    // class via a tool like ResGen or Visual Studio.
    // To add or remove a member, edit your .ResX file then rerun ResGen
    // with the /str option, or rebuild your VS project.
    [global::System.CodeDom.Compiler.GeneratedCodeAttribute("System.Resources.Tools.StronglyTypedResourceBuilder", "2.0.0.0")]
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute()]
    [global::System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    internal class Resources {
        
        private static global::System.Resources.ResourceManager resourceMan;
        
        private static global::System.Globalization.CultureInfo resourceCulture;
        
        [global::System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        internal Resources() {
        }
        
        /// <summary>
        ///   Returns the cached ResourceManager instance used by this class.
        /// </summary>
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        internal static global::System.Resources.ResourceManager ResourceManager {
            get {
                if (object.ReferenceEquals(resourceMan, null)) {
                    global::System.Resources.ResourceManager temp = new global::System.Resources.ResourceManager("Mesh4n.Adapters.HttpService.Properties.Resources", typeof(Resources).Assembly);
                    resourceMan = temp;
                }
                return resourceMan;
            }
        }
        
        /// <summary>
        ///   Overrides the current thread's CurrentUICulture property for all
        ///   resource lookups using this strongly typed resource class.
        /// </summary>
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        internal static global::System.Globalization.CultureInfo Culture {
            get {
                return resourceCulture;
            }
            set {
                resourceCulture = value;
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The item id in the URI is not the same as the item id in the payload. Uri {0}, Payload {1}.
        /// </summary>
        internal static string DifferentIds {
            get {
                return ResourceManager.GetString("DifferentIds", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The request does not contain any item to merge..
        /// </summary>
        internal static string EmptyRequest {
            get {
                return ResourceManager.GetString("EmptyRequest", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The feed {0} was not found.
        /// </summary>
        internal static string FeedNotFound {
            get {
                return ResourceManager.GetString("FeedNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Configuration file {0} has invalid content. {1}.
        /// </summary>
        internal static string InvalidConfigurationFile {
            get {
                return ResourceManager.GetString("InvalidConfigurationFile", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The provided type {0} does not implement the IFeedConfigurationManager interface.
        /// </summary>
        internal static string InvalidConfigurationManagerType {
            get {
                return ResourceManager.GetString("InvalidConfigurationManagerType", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The configuration path must be a folder. Configured path {0}.
        /// </summary>
        internal static string InvalidConfigurationPath {
            get {
                return ResourceManager.GetString("InvalidConfigurationPath", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The provided type {0} does not implement the ISyncAdapter interface.
        /// </summary>
        internal static string InvalidSyncAdapterType {
            get {
                return ResourceManager.GetString("InvalidSyncAdapterType", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The item {0} was not found.
        /// </summary>
        internal static string ItemNotFound {
            get {
                return ResourceManager.GetString("ItemNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to List of available feedsync feeds.
        /// </summary>
        internal static string MainFeedDescription {
            get {
                return ResourceManager.GetString("MainFeedDescription", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to FeedSync Feeds.
        /// </summary>
        internal static string MainFeedTitle {
            get {
                return ResourceManager.GetString("MainFeedTitle", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to There are not available items for the specified date.
        /// </summary>
        internal static string NoAvailableItems {
            get {
                return ResourceManager.GetString("NoAvailableItems", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The specified format {0} is not supported by the service..
        /// </summary>
        internal static string NotSupportedFormat {
            get {
                return ResourceManager.GetString("NotSupportedFormat", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to A configuration folder must be specified for the sync service. .
        /// </summary>
        internal static string NullOrEmptyConfigurationPath {
            get {
                return ResourceManager.GetString("NullOrEmptyConfigurationPath", resourceCulture);
            }
        }
    }
}
