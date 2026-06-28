export function Footer() {
  return (
    <footer className="fixed bottom-0 left-0 right-0 z-50 pointer-events-none">
      <div className="mx-auto max-w-7xl px-6 py-3">
        <div
          className="flex items-center justify-center rounded-2xl px-6 py-2.5
                     bg-white/30 backdrop-blur-xl border border-white/40 shadow-glass"
          style={{ opacity: 0.7 }}
        >
          <p className="text-sm font-medium text-slate-600 tracking-wide">
            Built by <span className="text-apple-blue font-semibold">Shivansh Bagga</span>
          </p>
        </div>
      </div>
    </footer>
  )
}
