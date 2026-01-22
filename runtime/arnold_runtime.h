/*
 * ArnoldC Native Runtime Header
 * "GET TO THE CHOPPER" - Bare-metal support for ArnoldC kernels
 * 
 * Include this in your kernel to provide runtime functions for
 * ArnoldC's TALK TO THE HAND and other operations.
 */

#ifndef ARNOLD_RUNTIME_H
#define ARNOLD_RUNTIME_H

/*
 * Types for freestanding environment
 * "THIS IS A WARRIOR" - Type definitions
 */
typedef unsigned char      uint8_t;
typedef unsigned short     uint16_t;
typedef unsigned int       uint32_t;
#ifdef __GNUC__
typedef unsigned long long uint64_t;
typedef long long          int64_t;
#endif
typedef signed char        int8_t;
typedef signed short       int16_t;
typedef signed int         int32_t;
typedef uint32_t           size_t;
typedef int32_t            ptrdiff_t;
typedef uint32_t           uintptr_t;

#ifndef __cplusplus
typedef uint8_t            bool;
#define true  1
#define false 0
#endif

#define NULL ((void*)0)

/*
 * VGA Text Mode Constants
 * "TALK TO THE HAND" - VGA memory access
 */
#define VGA_WIDTH   80
#define VGA_HEIGHT  25
#define VGA_MEMORY  ((uint16_t*)0xB8000)

/*
 * VGA Colors - "CONSIDER THAT A DIVORCE" from boring colors
 */
#define VGA_BLACK         0
#define VGA_BLUE          1
#define VGA_GREEN         2
#define VGA_CYAN          3
#define VGA_RED           4
#define VGA_MAGENTA       5
#define VGA_BROWN         6
#define VGA_LIGHT_GREY    7
#define VGA_DARK_GREY     8
#define VGA_LIGHT_BLUE    9
#define VGA_LIGHT_GREEN   10
#define VGA_LIGHT_CYAN    11
#define VGA_LIGHT_RED     12
#define VGA_LIGHT_MAGENTA 13
#define VGA_YELLOW        14
#define VGA_WHITE         15

/*
 * Terminal State
 */
static size_t term_row = 0;
static size_t term_col = 0;
static uint8_t term_color = VGA_LIGHT_GREEN | (VGA_BLACK << 4);

/*
 * VGA Entry Helper
 */
static inline uint16_t vga_entry(char c, uint8_t color) {
    return (uint16_t)c | ((uint16_t)color << 8);
}

/*
 * Terminal Functions - "TALK TO THE HAND"
 */
static void term_scroll(void) {
    // Scroll up by one line
    for (size_t y = 0; y < VGA_HEIGHT - 1; y++) {
        for (size_t x = 0; x < VGA_WIDTH; x++) {
            VGA_MEMORY[y * VGA_WIDTH + x] = VGA_MEMORY[(y + 1) * VGA_WIDTH + x];
        }
    }
    // Clear the last line
    for (size_t x = 0; x < VGA_WIDTH; x++) {
        VGA_MEMORY[(VGA_HEIGHT - 1) * VGA_WIDTH + x] = vga_entry(' ', term_color);
    }
}

static void term_putchar(char c) {
    if (c == '\n') {
        term_col = 0;
        term_row++;
        if (term_row >= VGA_HEIGHT) {
            term_scroll();
            term_row = VGA_HEIGHT - 1;
        }
        return;
    }
    
    if (c == '\r') {
        term_col = 0;
        return;
    }
    
    if (c == '\t') {
        term_col = (term_col + 8) & ~7;
        if (term_col >= VGA_WIDTH) {
            term_col = 0;
            term_row++;
            if (term_row >= VGA_HEIGHT) {
                term_scroll();
                term_row = VGA_HEIGHT - 1;
            }
        }
        return;
    }
    
    VGA_MEMORY[term_row * VGA_WIDTH + term_col] = vga_entry(c, term_color);
    term_col++;
    
    if (term_col >= VGA_WIDTH) {
        term_col = 0;
        term_row++;
        if (term_row >= VGA_HEIGHT) {
            term_scroll();
            term_row = VGA_HEIGHT - 1;
        }
    }
}

/*
 * Print String - "TALK TO THE HAND"
 */
static void arnold_print(const char* str) {
    while (*str) {
        term_putchar(*str++);
    }
}

/*
 * Print Integer - "TALK TO THE HAND" with numbers
 */
static void arnold_print_int(int value) {
    if (value < 0) {
        term_putchar('-');
        value = -value;
    }
    
    if (value == 0) {
        term_putchar('0');
        return;
    }
    
    char buf[12];
    int i = 0;
    
    while (value > 0) {
        buf[i++] = '0' + (value % 10);
        value /= 10;
    }
    
    while (i > 0) {
        term_putchar(buf[--i]);
    }
}

/*
 * Print Hex - "TALK TO THE HAND" with hex
 */
static void arnold_print_hex(uint32_t value) {
    const char hex[] = "0123456789ABCDEF";
    term_putchar('0');
    term_putchar('x');
    for (int i = 7; i >= 0; i--) {
        term_putchar(hex[(value >> (i * 4)) & 0xF]);
    }
}

/*
 * Read Integer - Stub for kernel mode
 * "I WANT TO ASK YOU A BUNCH OF QUESTIONS..."
 */
static int arnold_read_int(void) {
    // In kernel mode, this would need keyboard driver
    return 0;
}

/*
 * Clear Screen - "CHILL"
 */
static void arnold_clear(void) {
    for (size_t y = 0; y < VGA_HEIGHT; y++) {
        for (size_t x = 0; x < VGA_WIDTH; x++) {
            VGA_MEMORY[y * VGA_WIDTH + x] = vga_entry(' ', term_color);
        }
    }
    term_row = 0;
    term_col = 0;
}

/*
 * Set Color
 */
static void arnold_set_color(uint8_t fg, uint8_t bg) {
    term_color = fg | (bg << 4);
}

/*
 * I/O Port Functions - "DO IT NOW"
 */
static inline void outb(uint16_t port, uint8_t val) {
    __asm__ volatile ("outb %0, %1" : : "a"(val), "Nd"(port));
}

static inline uint8_t inb(uint16_t port) {
    uint8_t ret;
    __asm__ volatile ("inb %1, %0" : "=a"(ret) : "Nd"(port));
    return ret;
}

static inline void outw(uint16_t port, uint16_t val) {
    __asm__ volatile ("outw %0, %1" : : "a"(val), "Nd"(port));
}

static inline uint16_t inw(uint16_t port) {
    uint16_t ret;
    __asm__ volatile ("inw %1, %0" : "=a"(ret) : "Nd"(port));
    return ret;
}

static inline void outl(uint16_t port, uint32_t val) {
    __asm__ volatile ("outl %0, %1" : : "a"(val), "Nd"(port));
}

static inline uint32_t inl(uint16_t port) {
    uint32_t ret;
    __asm__ volatile ("inl %1, %0" : "=a"(ret) : "Nd"(port));
    return ret;
}

/*
 * I/O Wait - For slow devices
 */
static inline void io_wait(void) {
    outb(0x80, 0);
}

/*
 * Interrupt Control - "EVERYBODY CHILL" / "LET'S PARTY"
 */
static inline void arnold_cli(void) { __asm__ volatile ("cli"); }
static inline void arnold_sti(void) { __asm__ volatile ("sti"); }
static inline void arnold_hlt(void) { __asm__ volatile ("hlt"); }

/*
 * Memory Functions - "I NEED YOUR MEMORY"
 */
static void* arnold_memset(void* dest, int val, size_t count) {
    uint8_t* d = (uint8_t*)dest;
    while (count--) {
        *d++ = (uint8_t)val;
    }
    return dest;
}

static void* arnold_memcpy(void* dest, const void* src, size_t count) {
    uint8_t* d = (uint8_t*)dest;
    const uint8_t* s = (const uint8_t*)src;
    while (count--) {
        *d++ = *s++;
    }
    return dest;
}

static int arnold_memcmp(const void* s1, const void* s2, size_t count) {
    const uint8_t* p1 = (const uint8_t*)s1;
    const uint8_t* p2 = (const uint8_t*)s2;
    while (count--) {
        if (*p1 != *p2) {
            return *p1 - *p2;
        }
        p1++;
        p2++;
    }
    return 0;
}

/*
 * Simple heap allocator - "I NEED YOUR MEMORY"
 * This is a very basic bump allocator for kernel use
 */
static uint8_t* heap_ptr = (uint8_t*)0x100000;  // Start at 1MB
static uint8_t* heap_end = (uint8_t*)0x200000;  // End at 2MB

static void* arnold_alloc(size_t size) {
    // Align to 4 bytes
    size = (size + 3) & ~3;
    
    if (heap_ptr + size > heap_end) {
        return NULL;  // Out of memory - "I NEED A VACATION"
    }
    
    void* ptr = heap_ptr;
    heap_ptr += size;
    return ptr;
}

static void arnold_free(void* ptr) {
    // Simple bump allocator doesn't support free
    // "YOU'RE LUGGAGE" - but we keep it anyway
    (void)ptr;
}

/*
 * String Functions
 */
static size_t arnold_strlen(const char* str) {
    size_t len = 0;
    while (*str++) len++;
    return len;
}

static int arnold_strcmp(const char* s1, const char* s2) {
    while (*s1 && (*s1 == *s2)) {
        s1++;
        s2++;
    }
    return *(unsigned char*)s1 - *(unsigned char*)s2;
}

static char* arnold_strcpy(char* dest, const char* src) {
    char* d = dest;
    while ((*d++ = *src++));
    return dest;
}

/*
 * Panic - "GET TO THE CHOPPER" but something went wrong
 */
static void arnold_panic(const char* message) {
    arnold_set_color(VGA_WHITE, VGA_RED);
    arnold_print("\n!!! KERNEL PANIC !!!\n");
    arnold_print(message);
    arnold_print("\n\"I'LL BE BACK\"... but not this time.\n");
    arnold_cli();
    while (1) {
        arnold_hlt();
    }
}

/*
 * Assert - For debugging
 */
#define arnold_assert(condition, message) \
    do { \
        if (!(condition)) { \
            arnold_panic("Assertion failed: " message); \
        } \
    } while (0)

/*
 * Entry Point - Called by bootloader
 * Your ArnoldC code generates arnold_main()
 */
extern void arnold_main(void);

#endif /* ARNOLD_RUNTIME_H */
