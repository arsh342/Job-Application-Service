// Shared Authentication Helper for Job Portal
// Include this in all HTML templates for seamless authentication

(function () {
  "use strict";

  // Authentication utility functions
  window.AuthHelper = {
    // Get JWT token from localStorage
    getToken: function () {
      return (
        localStorage.getItem("authToken") ||
        localStorage.getItem("jwtToken") ||
        sessionStorage.getItem("authToken") ||
        sessionStorage.getItem("jwtToken")
      );
    },

    // Set JWT token
    setToken: function (token) {
      localStorage.setItem("authToken", token);
      localStorage.setItem("jwtToken", token); // Keep for compatibility
    },

    // Remove JWT token
    removeToken: function () {
      localStorage.removeItem("authToken");
      localStorage.removeItem("jwtToken");
      sessionStorage.removeItem("authToken");
      sessionStorage.removeItem("jwtToken");
    },

    // Check if user is authenticated
    isAuthenticated: function () {
      const token = this.getToken();
      return token && token.length > 0;
    },

    // Get auth headers for API calls
    getAuthHeaders: function () {
      const token = this.getToken();
      return token ? { Authorization: `Bearer ${token}` } : {};
    },

    // Redirect to login if not authenticated
    requireAuth: function () {
      if (!this.isAuthenticated()) {
        const authUrl = window.location.hostname === 'localhost'
          ? 'http://localhost:8083'
          : 'https://job-application-service-production.up.railway.app';
        window.location.href = `${authUrl}/login`;
        return false;
      }
      return true;
    },

    // Handle navigation with authentication
    navigateWithAuth: function (url) {
      if (this.isAuthenticated()) {
        const token = this.getToken();
        // Check if URL already has parameters
        const separator = url.includes("?") ? "&" : "?";
        window.location.href = `${url}${separator}token=${encodeURIComponent(
          token
        )}`;
      } else {
        const authUrl = window.location.hostname === 'localhost'
          ? 'http://localhost:8083'
          : 'https://job-application-service-production.up.railway.app';
        window.location.href = `${authUrl}/login`;
      }
    },

    // Setup automatic token injection for page navigation
    setupAuthInterceptor: function () {
      // Override click events for navigation links
      document.addEventListener("DOMContentLoaded", function () {
        // Intercept all navigation links
        document.addEventListener("click", function (e) {
          const link = e.target.closest("a");
          if (link && link.href) {
            const url = new URL(link.href);

            // Check if it's an internal navigation (dashboard, profile, etc.)
            const internalPaths = [
              "/dashboard",
              "/profile",
              "/browse-jobs",
              "/my-applications",
              "/create-job",
              "/job-details",
              "/job-listings",
            ];

            if (internalPaths.some((path) => url.pathname.includes(path))) {
              e.preventDefault();
              window.AuthHelper.navigateWithAuth(link.href);
            }
          }
        });

        // Handle form submissions with auth headers
        document.addEventListener("submit", function (e) {
          const form = e.target;
          if (form.method && form.method.toLowerCase() !== "get") {
            // Add token to form if authenticated
            if (window.AuthHelper.isAuthenticated()) {
              const token = window.AuthHelper.getToken();
              const tokenInput = document.createElement("input");
              tokenInput.type = "hidden";
              tokenInput.name = "token";
              tokenInput.value = token;
              form.appendChild(tokenInput);
            }
          }
        });
      });
    },

    // Extract token from URL parameters and store it
    handleTokenFromUrl: function () {
      const urlParams = new URLSearchParams(window.location.search);
      const urlToken = urlParams.get("token");

      if (urlToken) {
        this.setToken(urlToken);
        // Clean URL by removing token parameter
        urlParams.delete("token");
        const newUrl =
          window.location.pathname +
          (urlParams.toString() ? "?" + urlParams.toString() : "");
        window.history.replaceState({}, "", newUrl);
      }
    },

    // Initialize authentication helper
    init: function () {
      this.handleTokenFromUrl();
      this.setupAuthInterceptor();

      // Note: Automatic authentication check is disabled to prevent race conditions
      // The server-side AuthenticationFilter handles authentication for protected pages
      // This allows page-specific scripts to process tokens before any auth checks
    },
  };

  // Auto-initialize when script loads
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", function () {
      window.AuthHelper.init();
    });
  } else {
    window.AuthHelper.init();
  }
})();
