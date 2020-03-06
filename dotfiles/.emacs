;; load emacs 24's package system. Add MELPA repository.
(when (>= emacs-major-version 24)
  (require 'package)
  (add-to-list
   'package-archives
   ;; '("melpa" . "http://stable.melpa.org/packages/") ; many packages won't show if using stable
   '("melpa" . "http://melpa.milkbox.net/packages/")
   t))

;; Added by Package.el.  This must come before configurations of
;; installed packages.  Don't delete this line.  If you don't want it,
;; just comment it out by adding a semicolon to the start of the line.
;; You may delete these explanatory comments.
(package-initialize)

(custom-set-variables
 ;; custom-set-variables was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 '(ansi-color-faces-vector
   [default default default italic underline success warning error])
 '(ansi-color-names-vector
   ["black" "#d55e00" "#009e73" "#f8ec59" "#0072b2" "#cc79a7" "#56b4e9" "white"])
 '(custom-safe-themes
   (quote
    ("7675ffd2f5cb01a7aab53bcdd702fa019b56c764900f2eea0f74ccfc8e854386" "e61752b5a3af12be08e99d076aedadd76052137560b7e684a8be2f8d2958edc3" default)))
 '(package-selected-packages
   (quote
    (magit org centered-cursor-mode neotree paredit rainbow-delimiters cider)))
 '(show-paren-mode t))

(custom-set-faces
 ;; custom-set-faces was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 )

(and
      (require 'centered-cursor-mode)
      (global-centered-cursor-mode +1))

(global-hl-line-mode +1)
(menu-bar-mode 0)
(desktop-save-mode 1)

(add-hook 'cider-mode-hook #'eldoc-mode)

(global-set-key (kbd "C-c 0") 'neotree)
(global-set-key (kbd "<backtab>") 'other-window)

(load-theme 'moe-light)

(add-hook 'prog-mode-hook #'rainbow-delimiters-mode)
;;(add-hook 'prog-mode-hook #'paredit-mode)

(put 'erase-buffer 'disabled nil)
