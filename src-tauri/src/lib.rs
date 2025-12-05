#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
  use tauri::Emitter;
  tauri::Builder::default()
    .plugin(tauri_plugin_opener::init())
    .plugin(tauri_plugin_fs::init())
    .plugin(tauri_plugin_dialog::init())
    .setup(|app| {
      if cfg!(debug_assertions) {
        app.handle().plugin(
          tauri_plugin_log::Builder::default()
            .level(log::LevelFilter::Info)
            .build(),
        )?;
      }
      
      use tauri::menu::{Menu, MenuItem, Submenu, PredefinedMenuItem, AboutMetadata};
      let handle = app.handle();

      let app_menu = Submenu::with_items(
          handle,
          "MarkRight",
          true,
          &[
              &PredefinedMenuItem::about(handle, Some("About MarkRight"), Some(AboutMetadata::default()))?,
              &PredefinedMenuItem::separator(handle)?,
              &PredefinedMenuItem::services(handle, Some("Services"))?,
              &PredefinedMenuItem::separator(handle)?,
              &PredefinedMenuItem::hide(handle, Some("Hide MarkRight"))?,
              &PredefinedMenuItem::hide_others(handle, Some("Hide Others"))?,
              &PredefinedMenuItem::show_all(handle, Some("Show All"))?,
              &PredefinedMenuItem::separator(handle)?,
              &PredefinedMenuItem::quit(handle, Some("Quit MarkRight"))?,
          ],
      )?;
      
      let file_menu = Submenu::with_items(
          handle,
          "File",
          true,
          &[
              &MenuItem::with_id(handle, "open", "Open...", true, Some("CmdOrCtrl+O"))?,
              &MenuItem::with_id(handle, "save", "Save", true, Some("CmdOrCtrl+S"))?,
              &MenuItem::with_id(handle, "save_as", "Save As...", true, Some("CmdOrCtrl+Shift+S"))?,
          ],
      )?;

      let edit_menu = Submenu::with_items(
          handle,
          "Edit",
          true,
          &[
              &PredefinedMenuItem::undo(handle, Some("Undo"))?,
              &PredefinedMenuItem::redo(handle, Some("Redo"))?,
              &PredefinedMenuItem::separator(handle)?,
              &PredefinedMenuItem::cut(handle, Some("Cut"))?,
              &PredefinedMenuItem::copy(handle, Some("Copy"))?,
              &PredefinedMenuItem::paste(handle, Some("Paste"))?,
              &PredefinedMenuItem::select_all(handle, Some("Select All"))?,
          ],
      )?;

      let help_menu = Submenu::with_items(
          handle,
          "Help",
          true,
          &[
              &MenuItem::with_id(handle, "github", "MarkRight on GitHub", true, None::<&str>)?,
          ],
      )?;

      let menu = Menu::with_items(handle, &[&app_menu, &file_menu, &edit_menu, &help_menu])?;
      app.set_menu(menu)?;

      Ok(())
    })
    .on_menu_event(|app, event| {
        let id = event.id().as_ref();
        match id {
            "open" | "save" | "save_as" => {
                // Emit event to frontend
                let _ = app.emit(format!("menu-{}", id).as_str(), ());
            }
            "github" => {
                let _ = app.emit("menu-github", ());
            }
            _ => {}
        }
    })
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}
