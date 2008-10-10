;; example
;;
;; This is the basic example application that comes with Compojure for
;; demonstration purposes.

(ns example
  (:use (compojure jetty
		   html
                   http
		   validation)))

(defn template
  "A function to generate the standard outline of a HTML page."
  [title & body]
  (html
    (doctype :html4)
    [:html
      [:head
        [:title title]]
      [:body
        body]]))

(defn dummy-validator [params]
  {:password "Password must not be blank"
   :agree "must not be blank"
   "" "zipcode must be inside Texas"})

(def example-form
  (html-with-validator dummy-validator
    (doctype :xhtml-strict)
    [:html
     [:head
      (include-css "/public/test.css")
      [:title "Form"]]
     [:body 
      [:form {:method "post" :action "/form"}
       (validation-error-summary)
       [:p (label :name "Username:") " "
	(text-field :name "Anonymous")]
	       
       (decorate-errors :password
			[:p (label :password "Password:") " "
			 (password-field :password)])

       (decorate-errors :sex 
			[:p (label :sex "Sex:") " "
			 (drop-down :sex ["Male" "Female"])])

       [:p (label :profile "Profile:") [:br]
	(text-area {:cols 40 :rows 10} :profile)]

       (decorate-errors :agree 
			[:p (label :agree "Have read usage agreement:") " "
			 (check-box :agree)])
       [:p (submit-button "New User")
	(reset-button "Reset Form")]]]]))

(defn welcome-page
  "A basic welcome page."
  []
  (template "Greeting"
    [:h1#title "Welcome to Compojure"]
    [:p.info
      "Compojure is an open source web framework for "
      (link-to "http://clojure.org" "Clojure") "."]
    [:p
      "Here is an " (link-to "/form" "example of a form")
      " generated by Compojure."]))

(defservlet example-servlet
  "A Compojure example servlet."
  (GET "/"
    (welcome-page))
  (GET "/form"
       (render example-form))
  (POST "/form"
	(if (valid-html? example-form params)
	  (template "Form Validation" (html [:p "You are a genius!"]))
	  (do
	    (println "POST, rendering with errors")
	    (render example-form {:validate true :params params}))))

  (GET "/public/*"
       (serve-file "public" (route :*)))
  (ANY "*"
       (page-not-found)))

